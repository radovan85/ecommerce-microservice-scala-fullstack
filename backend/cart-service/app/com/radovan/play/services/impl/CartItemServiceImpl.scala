package com.radovan.play.services.impl

import com.radovan.play.brokers.CartNatsSender
import com.radovan.play.converter.TempConverter
import com.radovan.play.dto.CartItemDto
import com.radovan.play.exceptions.{InstanceUndefinedException, OperationNotAllowedException, OutOfStockException}
import com.radovan.play.repositories.CartItemRepository
import com.radovan.play.services.{CartItemService, CartService}
import jakarta.inject.{Inject, Provider, Singleton}


@Singleton
class CartItemServiceImpl extends CartItemService{

  private var itemRepository:CartItemRepository = _
  private var tempConverter:TempConverter = _
  private var cartServiceProvider: Provider[CartService] = _
  private var natsSender:CartNatsSender = _


  @Inject
  private def initialize(itemRepository: CartItemRepository,tempConverter: TempConverter,
                         cartServiceProvider:Provider[CartService], natsSender: CartNatsSender
                        ):Unit = {
    this.itemRepository = itemRepository
    this.tempConverter = tempConverter
    this.cartServiceProvider = cartServiceProvider
    this.natsSender = natsSender
  }

  private def cartService: CartService = cartServiceProvider.get()


  override def removeAllByCartId(cartId: Integer): Unit = {
    itemRepository.deleteAllByCartId(cartId)
    cartService.refreshCartState(cartId)
  }

  override def removeAllByProductId(productId: Integer): Unit = {
    itemRepository.deleteAllByProductId(productId)
    cartService.refreshAllCarts()
  }

  override def listAllByCartId(cartId: Integer): Array[CartItemDto] = {
    itemRepository.findAllByCartId(cartId).collect{
      case itemEntity => tempConverter.cartItemEntityToDto(itemEntity)
    }
  }

  override def listAllByProductId(productId: Integer): Array[CartItemDto] = {
    itemRepository.findAllByProductId(productId).collect{
      case itemEntity => tempConverter.cartItemEntityToDto(itemEntity)
    }
  }

  override def getItemById(itemId: Integer): CartItemDto = {
    itemRepository.findById(itemId) match {
      case Some(itemEntity) => tempConverter.cartItemEntityToDto(itemEntity)
      case None => throw new InstanceUndefinedException("Cart item has not been found")
    }
  }

  override def addCartItem(productId: Integer,jwtToken:String): CartItemDto = {
    val customerData = natsSender.retrieveCurrentCustomer(jwtToken)
    val productData = natsSender.retrieveProductFromBroker(productId, jwtToken)

    val cartId = Option(customerData.get("cartId")).map(_.asInt())
      .getOrElse(throw new InstanceUndefinedException("Missing cartId in customer data"))

    val cart = cartService.getCartById(cartId)

    val productNode = Option(productData.get("product"))
      .getOrElse(throw new InstanceUndefinedException("Product data is missing"))

    val unitStock     = productNode.get("unitStock").asInt()
    val productName   = productNode.get("productName").asText()
    val productPrice  = productNode.get("productPrice").floatValue()
    val discount      = productNode.get("discount").floatValue()

    val existingItemOpt = listAllByCartId(cartId)
      .find(_.getProductId() == productId)

    val cartItem = existingItemOpt match {
      case Some(item) =>
        item.setQuantity(item.getQuantity() + 1)
        item
      case None =>
        val newItem = new CartItemDto()
        newItem.setProductId(productId)
        newItem.setCartId(cartId)
        newItem.setQuantity(1)
        newItem
    }

    if (unitStock < cartItem.getQuantity()) {
      throw new OutOfStockException(s"There is a shortage of $productName in stock!")
    }

    val finalPrice = {
      val discounted = productPrice - ((productPrice * discount) / 100)
      discounted * cartItem.getQuantity()
    }

    cartItem.setPrice(finalPrice)

    val cartItemEntity = tempConverter.cartItemDtoToEntity(cartItem)
    cartItemEntity.setCart(tempConverter.cartDtoToEntity(cart))
    val storedItem = itemRepository.save(cartItemEntity)

    cartService.refreshCartState(cartId)

    tempConverter.cartItemEntityToDto(storedItem)
  }

  override def removeCartItem(itemId: Integer, jwtToken:String): Unit = {
    val customerData = natsSender.retrieveCurrentCustomer(jwtToken)
    val cartId = customerData.get("cartId").asInt()
    val cartItem = getItemById(itemId)

    if (cartItem.getCartId() != cartId) {
      throw new OperationNotAllowedException("Operation not allowed!")
    }

    itemRepository.deleteById(itemId)
    cartService.refreshCartState(cartId)
  }

  override def updateAllByProductId(productId: Integer,jwtToken:String): Unit = {

    val productData = natsSender.retrieveProductFromBroker(productId, jwtToken)
    val productDetails = Option(productData.get("product"))
      .getOrElse(throw new RuntimeException("Product details missing"))

    val productPrice = productDetails.get("productPrice").floatValue()
    val discount     = productDetails.get("discount").floatValue()

    val allItems = listAllByProductId(productId)
      .map(tempConverter.cartItemDtoToEntity)

    allItems.foreach { cartItem =>
      val finalPrice = (productPrice - (productPrice * discount / 100)) * cartItem.getQuantity()
      cartItem.setPrice(finalPrice)
      itemRepository.save(cartItem)
    }

    cartService.refreshAllCarts()
  }
}
