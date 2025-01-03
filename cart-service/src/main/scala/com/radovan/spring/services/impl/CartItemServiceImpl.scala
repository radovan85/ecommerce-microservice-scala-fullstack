package com.radovan.spring.services.impl

import com.fasterxml.jackson.databind.JsonNode
import com.radovan.spring.converter.{DeserializeConverter, TempConverter}
import com.radovan.spring.dto.CartItemDto
import com.radovan.spring.exceptions.{InstanceUndefinedException, OperationNotAllowedException, OutOfStockException}
import com.radovan.spring.repositories.CartItemRepository
import com.radovan.spring.services.{CartItemService, CartService}
import com.radovan.spring.utils.ServiceUrlProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import scala.jdk.CollectionConverters._

@Service
class CartItemServiceImpl extends CartItemService {

  private var cartService:CartService = _
  private var itemRepository:CartItemRepository = _
  private var deserializeConverter:DeserializeConverter = _
  private var tempConverter:TempConverter = _
  private var urlProvider:ServiceUrlProvider = _

  @Autowired
  private def initialize(cartService: CartService,itemRepository: CartItemRepository,
                         deserializeConverter: DeserializeConverter,tempConverter: TempConverter,
                         urlProvider: ServiceUrlProvider):Unit = {
    this.cartService = cartService
    this.itemRepository = itemRepository
    this.deserializeConverter = deserializeConverter
    this.tempConverter = tempConverter
    this.urlProvider = urlProvider
  }

  @Transactional
  override def addCartItem(productId: Integer): CartItemDto = {
    // Fetch the current customer's cart ID
    val customerUrl = s"${urlProvider.getCustomerServiceUrl}/customers/currentCustomer"
    val customerResponse = deserializeConverter.getJsonNodeResponse(customerUrl)
    val customerMap = deserializeConverter.deserializeJson(customerResponse.getBody.toString).asScala
    val cartId = customerMap("cartId").toString.toInt

    // Fetch product details
    val productUrl = s"${urlProvider.getProductServiceUrl}/products/productDetails/$productId"
    val productResponse = deserializeConverter.getJsonNodeResponse(productUrl)
    val productMap = deserializeConverter.deserializeJson(productResponse.getBody.toString).asScala
    val unitStock = productMap("unitStock").toString.toInt
    val productName = productMap("productName").toString

    // Check if the cart already contains the item
    val existingItem = listAllByCartId(cartId).find(_.getProductId == productId)

    // Update quantity or create a new cart item
    val cartItem = existingItem match {
      case Some(item) =>
        item.setQuantity(item.getQuantity + 1)
        item
      case None =>
        val newItem = new CartItemDto
        newItem.setProductId(productId)
        newItem.setCartId(cartId)
        newItem.setQuantity(1)
        newItem
    }

    // Check stock availability
    if (unitStock < cartItem.getQuantity) {
      throw new OutOfStockException(new Error(s"There is a shortage of $productName in stock!"))
    }

    // Save the cart item and refresh the cart state
    val cartItemEntity = tempConverter.cartItemDtoToEntity(cartItem)
    val storedItem = itemRepository.save(cartItemEntity)
    val returnValue = tempConverter.cartItemEntityToDto(storedItem)
    cartService.refreshCartState(cartId)
    returnValue
  }

  @Transactional
  override def removeCartItem(itemId: Integer): Unit = {
    val customerUrl = s"${urlProvider.getCustomerServiceUrl}/customers/currentCustomer"
    val customerResponse = deserializeConverter.getJsonNodeResponse(customerUrl)
    val customerMap = deserializeConverter.deserializeJson(customerResponse.getBody.toString).asScala
    val cartItem = getItemById(itemId)

    // Ekstrakcija stvarne vrednosti iz Option
    val cartIdOption = customerMap.get("cartId").map(_.toString.toInt)
    val cartId = cartIdOption.getOrElse(
      throw new IllegalArgumentException("Cart ID is missing or invalid!")
    )

    if (cartId != cartItem.getCartId) {
      throw new OperationNotAllowedException(new Error("Operation not allowed!"))
    }
    itemRepository.deleteById(itemId)
    cartService.refreshCartState(cartId)
  }


  @Transactional
  override def removeAllByCartId(cartId: Integer): Unit = {
    itemRepository.deleteAllByCartId(cartId)
    itemRepository.flush()
  }

  @Transactional
  override def removeAllByProductId(productId: Integer): Unit = {
    itemRepository.deleteAllByProductId(productId)
    itemRepository.flush()
    cartService.refreshAllCarts
  }

  @Transactional(readOnly = true)
  override def listAllByCartId(cartId: Integer): Array[CartItemDto] = {
    val allCartItems = itemRepository.findAllByCartId(cartId).asScala
    allCartItems.collect{
      case itemEntity => tempConverter.cartItemEntityToDto(itemEntity)
    }.toArray
  }

  @Transactional(readOnly = true)
  override def listAllByProductId(productId: Integer): Array[CartItemDto] = {
    val allCartItems = itemRepository.findAllByProductId(productId).asScala
    allCartItems.collect {
      case itemEntity => tempConverter.cartItemEntityToDto(itemEntity)
    }.toArray
  }

  @Transactional(readOnly = true)
  override def getItemById(itemId: Integer): CartItemDto = {
    val itemEntity = itemRepository.findById(itemId)
      .orElseThrow(() => new InstanceUndefinedException(new Error("The item has not been found!")))
    tempConverter.cartItemEntityToDto(itemEntity)
  }

  @Transactional
  override def updateItem(itemId: Integer, cartItem: JsonNode): CartItemDto = {
    val existingItem = getItemById(itemId)
    val existingItemEntity = tempConverter.cartItemDtoToEntity(existingItem)
    val itemMap = deserializeConverter.deserializeJson(cartItem.toString).asScala
    val priceOption = itemMap.get("price").map(_.toString.toFloat)
    val price = priceOption.getOrElse(
      throw new InstanceUndefinedException(new Error("Price has not been found"))
    )
    existingItemEntity.setPrice(price)
    val updatedItem = itemRepository.saveAndFlush(existingItemEntity)
    cartService.refreshCartState(updatedItem.getCart.getCartId)
    tempConverter.cartItemEntityToDto(updatedItem)
  }
}
