package com.radovan.scalatra.brokers

import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.fasterxml.jackson.databind.node.ObjectNode
import com.radovan.scalatra.exceptions.ExistingInstanceException
import com.radovan.scalatra.utils.NatsUtils
import io.nats.client.impl.Headers
import jakarta.inject.{Inject, Singleton}

import java.time.Duration

@Singleton
class CustomerNatsSender @Inject() (
                                     natsUtils: NatsUtils,
                                     objectMapper: ObjectMapper
                                   ) {

  private val USER_RESPONSE_QUEUE = "user.response"

  @throws[ExistingInstanceException]
  @throws[Exception]
  def sendUserCreate(userPayload: JsonNode): Int = {
    try {
      val payloadBytes = objectMapper.writeValueAsBytes(userPayload)
      val reply = natsUtils.getConnection
        .request("user.create", payloadBytes, Duration.ofSeconds(2))

      val response = objectMapper.readTree(reply.getData)
      val status = if (response.has("status")) response.get("status").asInt() else 500

      if (status == 200 && response.has("id")) {
        response.get("id").asInt()
      } else if (status == 409) {
        throw new ExistingInstanceException("Email already exists.")
      } else {
        val msg = if (response.has("message")) response.get("message").asText() else "Unknown error."
        throw new Exception(s"User creation failed: $msg")
      }

    } catch {
      case e: ExistingInstanceException => throw e
      case ex: Exception =>
        throw new Exception(s"NATS user.create failed: ${ex.getMessage}", ex)
    }
  }

  def sendDeleteUserEvent(userId: Int, jwtToken: String): Unit = {
    sendUserEvent(s"user.delete.$userId", userId, jwtToken)
  }

  def sendSuspendUserEvent(userId: Int, jwtToken: String): Unit = {
    sendUserEvent(s"user.suspend.$userId", userId, jwtToken)
  }

  def sendReactivateUserEvent(userId: Int, jwtToken: String): Unit = {
    sendUserEvent(s"user.reactivate.$userId", userId, jwtToken)
  }

  @throws[Exception]
  def retrieveCurrentUser(jwtToken: String): JsonNode = {
    try {
      val payload: ObjectNode = objectMapper.createObjectNode()
      payload.put("token", jwtToken)
      val payloadBytes = objectMapper.writeValueAsBytes(payload)

      val reply = natsUtils.getConnection
        .request("user.get", payloadBytes, Duration.ofSeconds(2))

      if (reply == null || reply.getData == null) {
        throw new RuntimeException("No reply received from user.get")
      }

      val response = objectMapper.readTree(reply.getData)
      val status = if (response.has("status")) response.get("status").asInt() else 200

      if (status >= 400) {
        val msg = if (response.has("message")) response.get("message").asText() else "Unknown error."
        throw new RuntimeException(s"Failed to fetch current user: $msg")
      }

      response

    } catch {
      case e: Exception =>
        throw new RuntimeException(s"Error retrieving current user: ${e.getMessage}", e)
    }
  }

  @throws[Exception]
  def sendDeleteAllOrders(cartId: Int, jwtToken: String): Unit = {
    try {
      val payload = objectMapper.createObjectNode()
      payload.put("Authorization", jwtToken)
      val payloadBytes = objectMapper.writeValueAsBytes(payload)

      val reply = natsUtils.getConnection
        .request(s"order.deleteAll.$cartId", payloadBytes, Duration.ofSeconds(2))

      if (reply == null || reply.getData == null) {
        throw new RuntimeException("No reply received from order.deleteAll")
      }

      val response = objectMapper.readTree(reply.getData)
      val status = if (response.has("status")) response.get("status").asInt() else 500

      if (status != 200) {
        val msg = if (response.has("message")) response.get("message").asText() else "Unknown error."
        throw new RuntimeException(s"Order deletion failed: $msg")
      }

    } catch {
      case ex: Exception =>
        throw new RuntimeException(s"NATS order.deleteAll failed: ${ex.getMessage}", ex)
    }
  }


  private def sendUserEvent(subject: String, userId: Int, jwtToken: String): Unit = {
    try {
      val payload = createUserEventPayload(userId)
      val headers = createAuthorizationHeaders(jwtToken)
      natsUtils.getConnection.publish(subject, headers, payload)
    } catch {
      case e: Exception =>
        throw new RuntimeException(s"Error sending user event: $subject", e)
    }
  }

  @throws[Exception]
  def sendCartDelete(cartId: Int, jwtToken: String): Unit = {
    try {
      val payload = objectMapper.createObjectNode()
      payload.put("Authorization", jwtToken)
      val payloadBytes = objectMapper.writeValueAsBytes(payload)

      val reply = natsUtils.getConnection
        .request(s"cart.delete.$cartId", payloadBytes, Duration.ofSeconds(2))

      if (reply == null || reply.getData == null) {
        throw new RuntimeException("No reply received from cart.delete")
      }

      val response = objectMapper.readTree(reply.getData)
      val status = if (response.has("status")) response.get("status").asInt() else 500

      if (status != 200) {
        val msg = if (response.has("message")) response.get("message").asText() else "Unknown error."
        throw new RuntimeException(s"Cart deletion failed: $msg")
      }

    } catch {
      case ex: Exception =>
        throw new RuntimeException(s"NATS cart.delete failed: ${ex.getMessage}", ex)
    }
  }


  private def createUserEventPayload(userId: Int): Array[Byte] = {
    val nodeRequest = objectMapper.createObjectNode()
    nodeRequest.put("userId", userId)
    objectMapper.writeValueAsBytes(nodeRequest)
  }

  private def createAuthorizationHeaders(jwtToken: String): Headers = {
    val headers = new Headers()
    headers.add("Nats-Reply-To", USER_RESPONSE_QUEUE)
    headers.add("Authorization", s"Bearer $jwtToken")
    headers
  }

  @throws[Exception]
  def sendCartCreate(): Int = {
    try {
      val emptyPayload = objectMapper.createObjectNode()
      val payloadBytes = objectMapper.writeValueAsBytes(emptyPayload)

      val reply = natsUtils.getConnection
        .request("cart.create", payloadBytes, Duration.ofSeconds(2))

      if (reply == null || reply.getData == null) {
        throw new RuntimeException("No reply received from cart.create")
      }

      val response = objectMapper.readTree(reply.getData)
      val status = if (response.has("status")) response.get("status").asInt() else 500

      if (status == 200 && response.has("id")) {
        response.get("id").asInt()
      } else {
        val msg = if (response.has("message")) response.get("message").asText() else "Unknown error."
        throw new Exception(s"Cart creation failed: $msg")
      }

    } catch {
      case ex: Exception =>
        throw new Exception(s"NATS cart.create failed: ${ex.getMessage}", ex)
    }
  }

}
