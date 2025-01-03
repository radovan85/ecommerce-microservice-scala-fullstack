package com.radovan.spring.services

import com.fasterxml.jackson.databind.JsonNode

trait OrderService {

  def addOrder:String

  def listAll:Array[JsonNode]

  def getOrderById(orderId:Integer):JsonNode

  def deleteOrder(orderId:Integer):String
}
