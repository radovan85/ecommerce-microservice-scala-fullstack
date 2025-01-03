package com.radovan.spring.services

import com.radovan.spring.dto.CartDto

trait CartService {

  def getCartById(cartId:Integer):CartDto

  def validateCart(cartId:Integer):CartDto

  def getMyCart:CartDto

  def calculateGrandTotal(cartId:Integer):Float

  def refreshCartState(cartId:Integer):Unit

  def refreshAllCarts:Unit

  def addCart:CartDto

  def clearCart:Unit

  def removeCart(cartId:Integer):Unit
}
