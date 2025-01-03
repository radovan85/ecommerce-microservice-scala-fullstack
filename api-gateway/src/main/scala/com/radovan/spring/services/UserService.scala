package com.radovan.spring.services

import com.fasterxml.jackson.databind.JsonNode

trait UserService {

  def authenticateUser(authRequest:JsonNode):JsonNode

  def listAll:Array[JsonNode]

  def getCurrentUser:JsonNode

  def getUserById(userId:Integer):JsonNode
}
