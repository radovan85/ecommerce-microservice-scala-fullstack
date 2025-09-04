package com.radovan.spring.converter

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.radovan.spring.dto.UserDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class DeserializeConverter {

  private var objectMapper: ObjectMapper = _

  @Autowired
  private def initialize(objectMapper: ObjectMapper): Unit = {
    this.objectMapper = objectMapper
  }

  def payloadToUserDto(payloadData: String): UserDto = {
    try {
      objectMapper.readValue(payloadData, classOf[UserDto])
    } catch {
      case e: Exception =>
        e.printStackTrace()
        null // možeš dodati bolji error handling po potrebi
    }
  }

  def deserializeUser(userDto: UserDto): String = {
    try {
      objectMapper.writeValueAsString(userDto)
    } catch {
      case e: JsonProcessingException =>
        e.printStackTrace()
        null
    }
  }
}

