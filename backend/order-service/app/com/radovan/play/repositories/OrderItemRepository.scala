package com.radovan.play.repositories

import com.radovan.play.entity.OrderItemEntity

trait OrderItemRepository {

  def findById(itemId:Integer):Option[OrderItemEntity]

  def findAllByOrderId(orderId:Integer):Array[OrderItemEntity]

  def save(itemEntity: OrderItemEntity):OrderItemEntity
}
