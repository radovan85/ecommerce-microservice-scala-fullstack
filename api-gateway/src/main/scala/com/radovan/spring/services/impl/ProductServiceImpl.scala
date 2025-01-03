package com.radovan.spring.services.impl

import com.fasterxml.jackson.databind.JsonNode
import com.radovan.spring.converter.DeserializeConverter
import com.radovan.spring.services.ProductService
import com.radovan.spring.utils.ServiceUrlProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpEntity, HttpMethod}
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class ProductServiceImpl extends ProductService {

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

  override def createProduct(product: JsonNode): String = {
    val url = s"${urlProvider.getProductServiceUrl}/products/createProduct"
    val requestEntity = new HttpEntity[JsonNode](product)
    val response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, classOf[String])
    response.getBody
  }

  override def getProductById(productId: Integer): JsonNode = {
    val url = s"${urlProvider.getProductServiceUrl}/products/productDetails/$productId"
    val response = deserializeConverter.getJsonNodeResponse(url)
    response.getBody
  }

  override def updateProduct(product: JsonNode, productId: Integer): String = {
    val url = s"${urlProvider.getProductServiceUrl}/products/updateProduct/$productId"
    val requestEntity = new HttpEntity[JsonNode](product)
    val response = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, classOf[String])
    response.getBody
  }

  override def deleteProduct(productId: Integer): String = {
    val url = s"${urlProvider.getProductServiceUrl}/products/deleteProduct/$productId"
    val response = restTemplate.exchange(url, HttpMethod.DELETE, null, classOf[String])
    response.getBody
  }

  override def listAll: Array[JsonNode] = {
    val url = s"${urlProvider.getProductServiceUrl}/products/allProducts"
    deserializeConverter.getJsonNodeArray(url)
  }

  override def listAllByCategoryId(categoryId: Integer): Array[JsonNode] = {
    val url = s"${urlProvider.getProductServiceUrl}/products/allProducts/$categoryId"
    deserializeConverter.getJsonNodeArray(url)
  }
}
