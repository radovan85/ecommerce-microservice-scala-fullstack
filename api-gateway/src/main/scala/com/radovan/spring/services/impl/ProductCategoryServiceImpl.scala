package com.radovan.spring.services.impl

import com.fasterxml.jackson.databind.JsonNode
import com.radovan.spring.converter.DeserializeConverter
import com.radovan.spring.services.ProductCategoryService
import com.radovan.spring.utils.ServiceUrlProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpEntity, HttpMethod}
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class ProductCategoryServiceImpl extends ProductCategoryService {

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

  override def addCategory(category: JsonNode): String = {
    val url = s"${urlProvider.getProductServiceUrl}/categories/addCategory"
    val requestEntity = new HttpEntity[JsonNode](category)
    val response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, classOf[String])
    response.getBody
  }

  override def getCategoryById(categoryId: Integer): JsonNode = {
    val url = s"${urlProvider.getProductServiceUrl}/categories/categoryDetails/$categoryId"
    val response = deserializeConverter.getJsonNodeResponse(url)
    response.getBody
  }

  override def updateCategory(category: JsonNode, categoryId: Integer): String = {
    val url = s"${urlProvider.getProductServiceUrl}/categories/updateCategory/$categoryId"
    val requestEntity = new HttpEntity[JsonNode](category)
    val response = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, classOf[String])
    response.getBody
  }

  override def deleteCategory(categoryId: Integer): String = {
    val url = s"${urlProvider.getProductServiceUrl}/categories/deleteCategory/$categoryId"
    val response = restTemplate.exchange(url, HttpMethod.DELETE, null, classOf[String])
    response.getBody
  }

  override def listAll: Array[JsonNode] = {
    val url = s"${urlProvider.getProductServiceUrl}/categories/allCategories"
    deserializeConverter.getJsonNodeArray(url)
  }
}
