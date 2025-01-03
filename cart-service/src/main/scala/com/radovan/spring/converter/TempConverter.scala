package com.radovan.spring.converter

import com.radovan.spring.dto.{CartDto, CartItemDto}
import com.radovan.spring.entity.{CartEntity, CartItemEntity}
import com.radovan.spring.repositories.{CartItemRepository, CartRepository}
import com.radovan.spring.utils.ServiceUrlProvider
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.text.DecimalFormat
import scala.collection.mutable.ArrayBuffer
import scala.jdk.CollectionConverters._

@Component
class TempConverter {

  private val decfor = new DecimalFormat("0.00")
  private var mapper:ModelMapper = _
  private var cartItemRepository:CartItemRepository = _
  private var deserializeConverter:DeserializeConverter = _
  private var urlProvider:ServiceUrlProvider = _
  private var cartRepository:CartRepository = _

  @Autowired
  private def initialize(mapper: ModelMapper,cartItemRepository: CartItemRepository,
                         deserializeConverter: DeserializeConverter,urlProvider: ServiceUrlProvider,
                         cartRepository: CartRepository):Unit = {
    this.mapper = mapper
    this.cartItemRepository = cartItemRepository
    this.deserializeConverter = deserializeConverter
    this.urlProvider = urlProvider
    this.cartRepository = cartRepository
  }

  def cartEntityToDto(cartEntity: CartEntity):CartDto = {
    val returnValue = mapper.map(cartEntity,classOf[CartDto])
    returnValue.setCartPrice(decfor.format(returnValue.getCartPrice).toFloat)
    val cartItemsOption = Option(cartEntity.getCartItems.asScala)
    val cartItemsIds = new ArrayBuffer[Integer]()
    cartItemsOption match {
      case Some(cartItems) =>
        cartItems.foreach(cartItemEntity => cartItemsIds += cartItemEntity.getCartItemId)
      case None =>
    }

    returnValue.setCartItemsIds(cartItemsIds.toArray)
    returnValue
  }

  def cartDtoToEntity(cartDto: CartDto):CartEntity = {
    val returnValue = mapper.map(cartDto,classOf[CartEntity])
    returnValue.setCartPrice(decfor.format(returnValue.getCartPrice).toFloat)
    val cartItemsIdsOption = Option(cartDto.getCartItemsIds)
    val cartItems = new ArrayBuffer[CartItemEntity]()
    cartItemsIdsOption match {
      case Some(cartItemsIds) =>
        cartItemsIds.foreach(itemId => {
          val cartItemEntity = cartItemRepository.findById(itemId).orElse(null)
          if (cartItemEntity != null) {
            cartItems += cartItemEntity
          }
        })
      case None =>
    }

    returnValue.setCartItems(cartItems.asJava)
    returnValue
  }

  def cartItemEntityToDto(cartItem: CartItemEntity): CartItemDto = {
    val returnValue = mapper.map(cartItem, classOf[CartItemDto])
    val productResponseOptional = Option(deserializeConverter.getJsonNodeResponse(s"${urlProvider.getProductServiceUrl}/products/productDetails/${cartItem.getProductId}"))

    productResponseOptional match {
      case Some(productResponse) =>
        val productMap = deserializeConverter.deserializeJson(productResponse.getBody.toString).asScala
        val discount = productMap("discount").toString.toFloat
        val productPrice = productMap("productPrice").toString.toFloat
        var itemPrice = productPrice - (productPrice * discount / 100)
        itemPrice *= cartItem.getQuantity
        returnValue.setPrice(decfor.format(itemPrice).toFloat)
        returnValue.setProductId(productMap("productId").toString.toInt)
      case None =>
    }

    val cartOption = Option(cartItem.getCart)
    if(cartOption.isDefined) returnValue.setCartId(cartOption.get.getCartId)
    returnValue
  }

  def cartItemDtoToEntity(cartItem: CartItemDto): CartItemEntity = {
    val returnValue = mapper.map(cartItem, classOf[CartItemEntity])
    val productResponseOption = Option(deserializeConverter.getJsonNodeResponse(s"${urlProvider.getProductServiceUrl}/products/productDetails/${cartItem.getProductId}"))

    productResponseOption match {
      case Some(productResponse) =>
        val productMap = deserializeConverter.deserializeJson(productResponse.getBody.toString).asScala
        val productId = productMap("productId").toString.toInt
        val discount = productMap("discount").toString.toFloat
        val productPrice = productMap("productPrice").toString.toFloat
        var itemPrice = productPrice - (productPrice * discount / 100)
        itemPrice *= cartItem.getQuantity
        returnValue.setPrice(decfor.format(itemPrice).toFloat)
        returnValue.setProductId(productId)
      case None =>
    }

    val cartIdOption = Option(cartItem.getCartId)
    cartIdOption match {
      case Some(cartId) =>
        val cartEntity = cartRepository.findById(cartId).orElse(null)
        if(cartEntity!=null) returnValue.setCart(cartEntity)
      case None =>
    }

    returnValue
  }
}
