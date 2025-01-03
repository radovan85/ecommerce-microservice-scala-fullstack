package com.radovan.spring.services.impl

import com.fasterxml.jackson.databind.JsonNode
import com.radovan.spring.converter.DeserializeConverter
import com.radovan.spring.services.CartService
import com.radovan.spring.utils.ServiceUrlProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class CartServiceImpl extends CartService {

  private var deserializeConverter:DeserializeConverter = _
  private var restTemplate:RestTemplate = _
  private var urlProvider:ServiceUrlProvider = _

  @Autowired
  private def initialize(deserializeConverter: DeserializeConverter,restTemplate: RestTemplate,
                         urlProvider: ServiceUrlProvider):Unit = {
    this.deserializeConverter = deserializeConverter
    this.restTemplate = restTemplate
    this.urlProvider = urlProvider
  }

  override def getMyCart: JsonNode = {
    val cartUrl = s"${urlProvider.getCartServiceUrl}/cart/getMyCart"
    val cartResponse = deserializeConverter.getJsonNodeResponse(cartUrl)
    cartResponse.getBody
  }

  override def clearCart: String = {
    val clearCartUrl = s"${urlProvider.getCartServiceUrl}/cart/clearCart"
    val response = restTemplate.exchange(clearCartUrl, HttpMethod.DELETE, null, classOf[String])
    response.getBody
  }
}
