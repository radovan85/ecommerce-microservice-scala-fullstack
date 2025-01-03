package com.radovan.spring.services.impl

import com.fasterxml.jackson.databind.JsonNode
import com.radovan.spring.converter.DeserializeConverter
import com.radovan.spring.services.UserService
import com.radovan.spring.utils.ServiceUrlProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpEntity, HttpMethod}
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class UserServiceImpl extends UserService {

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

  override def authenticateUser(authRequest: JsonNode): JsonNode = {
    val url = s"${urlProvider.getUserServiceUrl}/login"
    val requestEntity = new HttpEntity[JsonNode](authRequest)
    val response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, classOf[JsonNode])
    response.getBody
  }

  override def listAll: Array[JsonNode] = {
    val url = s"${urlProvider.getUserServiceUrl}/allUsers"
    deserializeConverter.getJsonNodeArray(url)
  }

  override def getCurrentUser: JsonNode = {
    val url = s"${urlProvider.getUserServiceUrl}/currentUser"
    val response = deserializeConverter.getJsonNodeResponse(url)
    response.getBody
  }

  override def getUserById(userId: Integer): JsonNode = {
    val url = s"${urlProvider.getUserServiceUrl}/userDetails/$userId"
    val response = deserializeConverter.getJsonNodeResponse(url)
    response.getBody
  }
}
