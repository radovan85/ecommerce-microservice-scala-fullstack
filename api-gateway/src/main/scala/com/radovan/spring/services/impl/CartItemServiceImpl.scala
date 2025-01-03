package com.radovan.spring.services.impl

import com.radovan.spring.services.CartItemService
import com.radovan.spring.utils.ServiceUrlProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class CartItemServiceImpl extends CartItemService {

  private var restTemplate:RestTemplate = _
  private var urlProvider:ServiceUrlProvider = _

  @Autowired
  private def initialize(restTemplate: RestTemplate,urlProvider: ServiceUrlProvider):Unit = {
    this.restTemplate = restTemplate
    this.urlProvider = urlProvider
  }

  override def addCartItem(productId: Integer): String = {
    val cartItemUrl = s"${urlProvider.getCartServiceUrl}/items/addItem/$productId"
    val response = restTemplate.exchange(cartItemUrl, HttpMethod.POST, null, classOf[String])
    response.getBody
  }

  override def deleteItem(itemId: Integer): String = {
    val deleteItemUrl = s"${urlProvider.getCartServiceUrl}/items/deleteItem/$itemId"
    val response = restTemplate.exchange(deleteItemUrl, HttpMethod.DELETE, null, classOf[String])
    response.getBody
  }
}
