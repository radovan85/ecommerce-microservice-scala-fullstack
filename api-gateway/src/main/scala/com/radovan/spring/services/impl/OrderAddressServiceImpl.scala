package com.radovan.spring.services.impl

import com.fasterxml.jackson.databind.JsonNode
import com.radovan.spring.converter.DeserializeConverter
import com.radovan.spring.services.OrderAddressService
import com.radovan.spring.utils.ServiceUrlProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class OrderAddressServiceImpl extends OrderAddressService {

  private var deserializeConverter: DeserializeConverter = _
  private var urlProvider: ServiceUrlProvider = _

  @Autowired
  private def initialize(deserializeConverter: DeserializeConverter, urlProvider: ServiceUrlProvider): Unit = {
    this.deserializeConverter = deserializeConverter
    this.urlProvider = urlProvider
  }

  override def listAll: Array[JsonNode] = {
    val url = s"${urlProvider.getOrderServiceUrl}/order/allAddresses"
    deserializeConverter.getJsonNodeArray(url)
  }
}
