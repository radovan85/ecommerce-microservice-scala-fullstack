package com.radovan.spring.services

import com.fasterxml.jackson.databind.JsonNode

trait OrderItemService {

  def listAllByOrderId(orderId:Integer):Array[JsonNode]
}
