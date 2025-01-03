package com.radovan.spring.services.impl

import com.fasterxml.jackson.databind.JsonNode
import com.radovan.spring.services.ShippingAddressService
import com.radovan.spring.utils.ServiceUrlProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpEntity, HttpMethod}
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class ShippingAddressServiceImpl extends ShippingAddressService {

  private var restTemplate: RestTemplate = _
  private var urlProvider: ServiceUrlProvider = _

  @Autowired
  private def initialize( restTemplate: RestTemplate, urlProvider: ServiceUrlProvider): Unit = {
    this.restTemplate = restTemplate
    this.urlProvider = urlProvider
  }

  override def updateAddress(address: JsonNode): String = {
    val url = s"${urlProvider.getCustomerServiceUrl}/addresses/updateAddress"
    val requestEntity = new HttpEntity[JsonNode](address)
    val response = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, classOf[String])
    response.getBody
  }
}
