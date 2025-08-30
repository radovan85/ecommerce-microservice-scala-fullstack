package com.radovan.play.converter

import com.radovan.play.dto.{CartDto, CartItemDto}
import com.radovan.play.entity.{CartEntity, CartItemEntity}
import com.radovan.play.repositories.{CartItemRepository, CartRepository}
import jakarta.inject.{Inject, Singleton}
import org.modelmapper.ModelMapper

import java.text.DecimalFormat
import scala.collection.mutable.ArrayBuffer
import scala.jdk.CollectionConverters._

@Singleton
class TempConverter {

  private var mapper: ModelMapper = _
  private var cartRepository: CartRepository = _
  private var itemRepository: CartItemRepository = _
  private val decfor = new DecimalFormat("0.00")

  @Inject
  private def initialize(mapper: ModelMapper, cartRepository: CartRepository,
                         itemRepository: CartItemRepository): Unit = {
    this.mapper = mapper
    this.cartRepository = cartRepository
    this.itemRepository = itemRepository
  }

  def cartEntityToDto(cartEntity: CartEntity): CartDto = {
    val returnValue = mapper.map(cartEntity, classOf[CartDto])
    returnValue.setCartPrice(decfor.format(returnValue.getCartPrice()).toFloat)
    val cartItemsOption = Option(cartEntity.getCartItems())
    val cartItemsIds = new ArrayBuffer[Integer]()
    cartItemsOption match {
      case Some(cartItems) =>
        cartItems.forEach(itemEntity => cartItemsIds += itemEntity.getCartItemId())
      case None =>
    }

    returnValue.setCartItemsIds(cartItemsIds.toArray)
    returnValue
  }

  def cartDtoToEntity(cartDto: CartDto): CartEntity = {
    val returnValue = mapper.map(cartDto, classOf[CartEntity])
    returnValue.setCartPrice(decfor.format(returnValue.getCartPrice()).toFloat)
    val cartItemsIdsOption = Option(cartDto.getCartItemsIds())
    val cartItems = new ArrayBuffer[CartItemEntity]()
    cartItemsIdsOption match {
      case Some(cartItemsIds) =>
        cartItemsIds.foreach(itemId => {
          itemRepository.findById(itemId) match {
            case Some(itemEntity) => cartItems += itemEntity
            case None =>
          }
        })
      case None =>
    }

    returnValue.setCartItems(cartItems.asJava)
    returnValue
  }

  def cartItemEntityToDto(itemEntity: CartItemEntity): CartItemDto = {
    val returnValue = mapper.map(itemEntity, classOf[CartItemDto])
    returnValue.setPrice(decfor.format(returnValue.getPrice()).toFloat)
    val cartOption = Option(itemEntity.getCart())
    cartOption match {
      case Some(cartEntity) => returnValue.setCartId(cartEntity.getCartId())
      case None =>
    }

    returnValue
  }

  def cartItemDtoToEntity(itemDto: CartItemDto): CartItemEntity = {
    val returnValue = mapper.map(itemDto, classOf[CartItemEntity])
    returnValue.setPrice(decfor.format(returnValue.getPrice()).toFloat)
    val cartIdOption = Option(itemDto.getCartId())
    cartIdOption match {
      case Some(cartId) =>
        cartRepository.findById(cartId) match {
          case Some(cartEntity) => returnValue.setCart(cartEntity)
          case None =>
        }
      case None =>
    }

    returnValue
  }

}
