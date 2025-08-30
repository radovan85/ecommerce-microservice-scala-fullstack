package com.radovan.play.services

import com.radovan.play.dto.OrderItemDto

trait OrderItemService {

  def listAllByOrderId(orderId:Int):Array[OrderItemDto]
}
