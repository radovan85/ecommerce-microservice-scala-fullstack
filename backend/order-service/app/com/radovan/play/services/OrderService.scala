package com.radovan.play.services

import com.radovan.play.dto.OrderDto

trait OrderService {

  def addOrder(jwtToken:String):OrderDto

  def getOrderById(orderId:Int):OrderDto

  def listAll:Array[OrderDto]

  def listAllByCartId(cartId:Int):Array[OrderDto]

  def deleteOrder(orderId:Int):Unit

  def deleteAllByCartId(cartId:Int):Unit
}
