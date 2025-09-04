package com.radovan.play.repositories

import com.radovan.play.entity.OrderEntity

trait OrderRepository {

  def findById(orderId:Integer):Option[OrderEntity]

  def deleteById(orderId:Integer):Unit

  def findAllByCartId(cartId:Integer):Array[OrderEntity]

  def findAll:Array[OrderEntity]

  def save(orderEntity: OrderEntity):OrderEntity
}
