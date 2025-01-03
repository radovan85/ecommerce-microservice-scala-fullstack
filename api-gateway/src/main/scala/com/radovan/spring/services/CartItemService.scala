package com.radovan.spring.services

trait CartItemService {

  def addCartItem(productId:Integer):String

  def deleteItem(itemId:Integer):String
}
