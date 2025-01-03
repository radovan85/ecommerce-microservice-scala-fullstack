package com.radovan.spring.services.impl

import com.radovan.spring.converter.{DeserializeConverter, TempConverter}
import com.radovan.spring.dto.CartDto
import com.radovan.spring.exceptions.{InstanceUndefinedException, InvalidCartException}
import com.radovan.spring.repositories.CartRepository
import com.radovan.spring.services.{CartItemService, CartService}
import com.radovan.spring.utils.ServiceUrlProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.text.DecimalFormat
import scala.jdk.CollectionConverters._

@Service
class CartServiceImpl extends CartService {

  private val decfor = new DecimalFormat("0.00")
  private var deserializeConverter:DeserializeConverter = _
  private var tempConverter:TempConverter = _
  private var cartRepository:CartRepository = _
  private var cartItemService:CartItemService = _
  private var urlProvider:ServiceUrlProvider = _

  @Autowired
  private def initialize(deserializeConverter: DeserializeConverter,tempConverter: TempConverter,
                         cartRepository: CartRepository,cartItemService: CartItemService,
                         urlProvider: ServiceUrlProvider):Unit = {
    this.deserializeConverter = deserializeConverter
    this.tempConverter = tempConverter
    this.cartRepository = cartRepository
    this.cartItemService = cartItemService
    this.urlProvider = urlProvider
  }

  @Transactional(readOnly = true)
  override def getCartById(cartId: Integer): CartDto = {
    val cartEntity = cartRepository.findById(cartId)
      .orElseThrow(() => new InstanceUndefinedException(new Error("The cart has not been found")))
    tempConverter.cartEntityToDto(cartEntity)
  }

  @Transactional(readOnly = true)
  override def validateCart(cartId: Integer): CartDto = {
    val returnValue = getCartById(cartId)
    if(returnValue.getCartItemsIds.isEmpty) throw new InvalidCartException(new Error("Your cart is currently empty!"))
    returnValue
  }

  @Transactional(readOnly = true)
  override def getMyCart: CartDto = {
    val customerUrl = s"${urlProvider.getCustomerServiceUrl}/customers/currentCustomer"
    val response = deserializeConverter.getJsonNodeResponse(customerUrl)
    val cartId = response.getBody.get("cartId").toString.toInt
    getCartById(cartId)
  }

  @Transactional(readOnly = true)
  override def calculateGrandTotal(cartId: Integer): Float = {
    var grandTotal = 0f
    val allCartItems = cartItemService.listAllByCartId(cartId)
    allCartItems.foreach(item => {
      val productUrl = s"${urlProvider.getProductServiceUrl}/products/${item.getProductId}"
      val productResponse = deserializeConverter.getJsonNodeResponse(productUrl)
      val productMap = deserializeConverter.deserializeJson(productResponse.getBody.toString).asScala
      val productPriceOption = productMap.get("productPrice").map(_.toString.toFloat)
      val productPrice = productPriceOption.getOrElse(
        throw new InstanceUndefinedException(new Error("Product price has not been found!"))
      )
      grandTotal = grandTotal + (productPrice * item.getQuantity)
    })

    decfor.format(grandTotal).toFloat
  }

  @Transactional
  override def refreshCartState(cartId: Integer): Unit = {
    val cart = getCartById(cartId)
    val cartPrice = cartRepository.calculateCartPrice(cartId).getOrElse(0f)
    cart.setCartPrice(cartPrice)
    cartRepository.saveAndFlush(tempConverter.cartDtoToEntity(cart))
  }

  @Transactional
  override def refreshAllCarts: Unit = {
    val allCarts = cartRepository.findAll().asScala
    allCarts.foreach(cartEntity => refreshCartState(cartEntity.getCartId))
  }

  @Transactional
  override def addCart: CartDto = {
    val cart = new CartDto
    cart.setCartPrice(0f)
    val storedCart = cartRepository.save(tempConverter.cartDtoToEntity(cart))
    tempConverter.cartEntityToDto(storedCart)
  }

  @Transactional
  override def clearCart: Unit = {
    val customerUrl = s"${urlProvider.getCustomerServiceUrl}/customers/currentCustomer"
    val customerResponse = deserializeConverter.getJsonNodeResponse(customerUrl)
    val customerMap = deserializeConverter.deserializeJson(customerResponse.getBody.toString).asScala
    val cartIdOption = customerMap.get("cartId").map(_.toString.toInt)
    cartIdOption match {
      case Some(cartId) =>
        cartItemService.removeAllByCartId(cartId)
        refreshCartState(cartId)
      case None => throw new InstanceUndefinedException(new Error("Cart ID is missing or invalid!"))
    }
  }

  @Transactional
  override def removeCart(cartId: Integer): Unit = {
    getCartById(cartId)
    cartRepository.deleteById(cartId)
    cartRepository.flush()
  }
}
