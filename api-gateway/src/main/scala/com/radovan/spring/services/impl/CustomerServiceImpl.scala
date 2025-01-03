package com.radovan.spring.services.impl

import com.fasterxml.jackson.databind.JsonNode
import com.radovan.spring.converter.DeserializeConverter
import com.radovan.spring.services.CustomerService
import com.radovan.spring.utils.ServiceUrlProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpEntity, HttpMethod}
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class CustomerServiceImpl extends CustomerService {

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

  override def addCustomer(registrationForm: JsonNode): JsonNode = {
    val customerUrl = s"${urlProvider.getCustomerServiceUrl}/customers/createCustomer"
    val requestEntity = new HttpEntity[JsonNode](registrationForm)
    val response = restTemplate.exchange(customerUrl, HttpMethod.POST, requestEntity, classOf[JsonNode])
    response.getBody
  }

  override def listAll: Array[JsonNode] = {
    val allCustomerUrl = s"${urlProvider.getCustomerServiceUrl}/customers/allCustomers"
    deserializeConverter.getJsonNodeArray(allCustomerUrl)
  }

  override def deleteCustomer(customerId: Integer): String = {
    val deleteCustomerUrl = s"${urlProvider.getCustomerServiceUrl}/customers/deleteCustomer/$customerId"
    val response = restTemplate.exchange(deleteCustomerUrl, HttpMethod.DELETE, null, classOf[String])
    response.getBody
  }

  override def suspendCustomer(customerId: Integer): String = {
    val url = s"${urlProvider.getCustomerServiceUrl}/customers/suspendCustomer/$customerId"
    val response = restTemplate.exchange(url, HttpMethod.PUT, null, classOf[String])
    response.getBody
  }

  override def reactivateCustomer(customerId: Integer): String = {
    val url = s"${urlProvider.getCustomerServiceUrl}/customers/reactivateCustomer/$customerId"
    val response = restTemplate.exchange(url, HttpMethod.PUT, null, classOf[String])
    response.getBody
  }
}
