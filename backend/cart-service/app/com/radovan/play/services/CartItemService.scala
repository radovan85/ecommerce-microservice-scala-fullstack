package com.radovan.play.services

import com.radovan.play.dto.CartItemDto


trait CartItemService {

  def removeAllByCartId(cartId: Integer): Unit

  def removeAllByProductId(productId: Integer): Unit

  def listAllByCartId(cartId: Integer): Array[CartItemDto]

  def listAllByProductId(productId: Integer): Array[CartItemDto]

  def getItemById(itemId: Integer): CartItemDto

  def removeCartItem(itemId: Integer, jwtToken:String): Unit

  def updateAllByProductId(productId: Integer, jwtToken:String): Unit

  def addCartItem(productId: Integer, jwtToken:String): CartItemDto
}
