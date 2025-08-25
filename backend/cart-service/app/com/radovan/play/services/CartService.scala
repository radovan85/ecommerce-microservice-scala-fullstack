package com.radovan.play.services

import com.radovan.play.dto.CartDto
import play.api.mvc.RequestHeader
import play.mvc.Http

trait CartService {

  def getCartById(cartId: Integer): CartDto

  def validateCart(cartId: Integer): CartDto

  def refreshCartState(cartId: Integer): Unit

  def refreshAllCarts(): Unit

  def addCart(): CartDto

  def clearCart(jwtToken: String): Unit

  def deleteCart(cartId: Integer): Unit
}
