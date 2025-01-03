package com.radovan.spring.services.impl

import com.fasterxml.jackson.databind.JsonNode
import com.radovan.spring.converter.DeserializeConverter
import com.radovan.spring.services.OrderService
import com.radovan.spring.utils.ServiceUrlProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class OrderServiceImpl extends OrderService {

  private var deserializeConverter: DeserializeConverter = _
  private var restTemplate: RestTemplate = _
  private var urlProvider: ServiceUrlProvider = _

  @Autowired
  private def initialize(deserializeConverter: DeserializeConverter, restTemplate: RestTemplate,
                         urlProvider: ServiceUrlProvider): Unit = {
    this.deserializeConverter = deserializeConverter
    this.restTemplate = restTemplate
    this.urlProvider = urlProvider
  }

  override def addOrder: String = {
    val url = s"${urlProvider.getOrderServiceUrl}/order/placeOrder"
    val response = restTemplate.exchange(url, HttpMethod.POST, null, classOf[String])
    response.getBody
  }

  override def listAll: Array[JsonNode] = {
    val url = s"${urlProvider.getOrderServiceUrl}/order/allOrders"
    deserializeConverter.getJsonNodeArray(url)
  }

  override def getOrderById(orderId: Integer): JsonNode = {
    val url = s"${urlProvider.getOrderServiceUrl}/order/orderDetails/$orderId"
    val response = deserializeConverter.getJsonNodeResponse(url)
    response.getBody
  }

  override def deleteOrder(orderId: Integer): String = {
    val url = s"${urlProvider.getOrderServiceUrl}/order/deleteOrder/$orderId"
    val response = restTemplate.exchange(url, HttpMethod.DELETE, null, classOf[String])
    response.getBody
  }
}
