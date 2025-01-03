package com.radovan.spring.services

import com.radovan.spring.dto.OrderDto

trait OrderService {

  def addOrder:OrderDto

  def getOrderById(orderId:Integer):OrderDto

  def listAll:Array[OrderDto]

  def listAllByCartId(cartId:Integer):Array[OrderDto]

  def deleteOrder(orderId:Integer):Unit

  def deleteAllByCartId(cartId:Integer):Unit
}
