package com.radovan.play.repositories

import com.radovan.play.entity.CartEntity

trait CartRepository {

  def calculateCartPrice(cartId:Integer):Option[Float]
  def findById(cartId:Integer):Option[CartEntity]
  def save(cartEntity: CartEntity):CartEntity
  def findAll:Array[CartEntity]
  def deleteById(cartId: Integer): Unit
}
