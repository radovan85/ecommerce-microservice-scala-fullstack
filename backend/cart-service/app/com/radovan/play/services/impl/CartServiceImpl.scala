package com.radovan.play.services.impl

import com.radovan.play.brokers.CartNatsSender
import com.radovan.play.converter.TempConverter
import com.radovan.play.dto.CartDto
import com.radovan.play.exceptions.{InstanceUndefinedException, InvalidCartException}
import com.radovan.play.repositories.CartRepository
import com.radovan.play.services.{CartItemService, CartService}
import jakarta.inject.Inject

class CartServiceImpl extends CartService{

  private var cartRepository:CartRepository = _
  private var tempConverter:TempConverter = _
  private var cartItemService:CartItemService = _
  private var natsSender:CartNatsSender = _


  @Inject
  private def initialize(cartRepository: CartRepository,tempConverter: TempConverter,
                         cartItemService: CartItemService,natsSender: CartNatsSender):Unit = {
    this.cartRepository = cartRepository
    this.tempConverter = tempConverter
    this.cartItemService = cartItemService
    this.natsSender = natsSender

  }

  override def getCartById(cartId: Integer): CartDto = {
    cartRepository.findById(cartId) match {
      case Some(cartEntity) => tempConverter.cartEntityToDto(cartEntity)
      case None => throw new InstanceUndefinedException("The cart has not been found!")
    }
  }

  override def validateCart(cartId: Integer): CartDto = {
    val returnValue = getCartById(cartId)
    if(returnValue.getCartItemsIds().isEmpty) throw new InvalidCartException("Your cart is currently empty!")
    returnValue
  }

  override def refreshCartState(cartId: Integer): Unit = {
    val cart = getCartById(cartId)
    val cartPrice = cartRepository.calculateCartPrice(cartId).getOrElse(0f)
    cart.setCartPrice(cartPrice)
    cartRepository.save(tempConverter.cartDtoToEntity(cart))
  }

  override def refreshAllCarts(): Unit = {
    cartRepository.findAll.foreach(cartEntity => refreshCartState(cartEntity.getCartId()))
  }

  override def addCart(): CartDto = {
    val cartDto = new CartDto
    cartDto.setCartPrice(0f)
    val storedCart = cartRepository.save(tempConverter.cartDtoToEntity(cartDto))
    tempConverter.cartEntityToDto(storedCart)
  }

  override def deleteCart(cartId: Integer): Unit = {
    getCartById(cartId)
    cartRepository.deleteById(cartId)
  }

  override def clearCart(jwtToken:String): Unit = {
    val customerData = natsSender.retrieveCurrentCustomer(jwtToken)
    val cartId = customerData.get("cartId").asInt()
    cartItemService.removeAllByCartId(cartId)
    refreshCartState(cartId)
  }










}
