package com.radovan.spring.converter

import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.{HttpEntity, HttpMethod, ResponseEntity}
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

import java.util
import scala.jdk.CollectionConverters._


@Component
class DeserializeConverter {

  @Autowired
  private var restTemplate:RestTemplate = _
  private val objectMapper = new ObjectMapper()

  def deserializeJson(jsonString: String): util.Map[String, Any] = {
    objectMapper.readValue(jsonString, classOf[util.Map[String, Any]])
  }

  def getJsonNodeResponse(url: String): ResponseEntity[JsonNode] = {
    restTemplate.getForEntity(url, classOf[JsonNode])
  }

  def getJsonNodeArray(url: String): Array[JsonNode] = {
    val entity = new HttpEntity[Unit](null)

    val response = restTemplate.exchange(
      url,
      HttpMethod.GET,
      entity,
      new ParameterizedTypeReference[util.List[JsonNode]]() {}
    )

    response.getBody.asScala.toArray
  }

}
