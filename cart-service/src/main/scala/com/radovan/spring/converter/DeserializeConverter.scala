package com.radovan.spring.converter

import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

import java.util

@Component
class DeserializeConverter {

  @Autowired
  private var restTemplate: RestTemplate = _
  private val objectMapper = new ObjectMapper()

  def deserializeJson(jsonString: String): util.Map[String, Any] = {
    objectMapper.readValue(jsonString, classOf[util.Map[String, Any]])
  }

  def getJsonNodeResponse(url: String): ResponseEntity[JsonNode] = {
    restTemplate.getForEntity(url, classOf[JsonNode])
  }
}
