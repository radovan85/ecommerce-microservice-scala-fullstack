package com.radovan.spring.services

import com.fasterxml.jackson.databind.JsonNode
import com.radovan.spring.dto.CartItemDto

trait CartItemService {

  def addCartItem(productId:Integer):CartItemDto

  def removeCartItem(itemId:Integer):Unit

  def removeAllByCartId(cartId:Integer):Unit

  def removeAllByProductId(productId:Integer):Unit

  def listAllByCartId(cartId:Integer):Array[CartItemDto]

  def listAllByProductId(productId:Integer):Array[CartItemDto]

  def getItemById(itemId:Integer):CartItemDto

  def updateItem(itemId:Integer,cartItem:JsonNode):CartItemDto
}
