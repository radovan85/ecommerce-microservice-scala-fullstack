package com.radovan.spring.services.impl

import com.fasterxml.jackson.databind.JsonNode
import com.radovan.spring.converter.DeserializeConverter
import com.radovan.spring.services.OrderItemService
import com.radovan.spring.utils.ServiceUrlProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class OrderItemServiceImpl extends OrderItemService {

  private var deserializeConverter: DeserializeConverter = _
  private var urlProvider: ServiceUrlProvider = _

  @Autowired
  private def initialize(deserializeConverter: DeserializeConverter, urlProvider: ServiceUrlProvider): Unit = {
    this.deserializeConverter = deserializeConverter
    this.urlProvider = urlProvider
  }

  override def listAllByOrderId(orderId: Integer): Array[JsonNode] = {
    val url = s"${urlProvider.getOrderServiceUrl}/order/allItems/$orderId"
    deserializeConverter.getJsonNodeArray(url)
  }
}
