package com.radovan.play.brokers

import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.radovan.play.utils.NatsUtils
import io.nats.client.Message
import jakarta.inject.{Inject, Singleton}

import java.nio.charset.StandardCharsets
import java.util.concurrent.{CompletableFuture, TimeUnit}

@Singleton
class CartNatsSender @Inject() (
                                 natsUtils: NatsUtils,
                                 objectMapper: ObjectMapper
                               ) {

  private val REQUEST_TIMEOUT_SECONDS = 5


  def retrieveProductFromBroker(productId: Int, jwtToken: String): JsonNode = {
    val payload = objectMapper.createObjectNode()
      .put("token", jwtToken)
      .put("productId", productId)

    val response = sendRequest(s"product.get.$productId", payload.toString)
    val json = objectMapper.readTree(response)

    if (json.has("status") && json.get("status").asInt() == 422) {
      val msg = if (json.has("message")) json.get("message").asText() else "Product not found"
      throw new RuntimeException(msg)
    }

    json
  }

  def retrieveCurrentCustomer(jwtToken: String): JsonNode = {
    val payload = objectMapper.createObjectNode()
      .put("token", jwtToken)

    val response = sendRequest("customer.getCurrent", payload.toString)
    val json = objectMapper.readTree(response)

    if (json.has("status") && json.get("status").asInt() == 500) {
      val msg = if (json.has("error")) json.get("error").asText() else "Failed to retrieve current customer"
      throw new RuntimeException(msg)
    }

    json
  }



  private def sendRequest(subject: String, payload: String): String = {
    val connection = natsUtils.getConnection
    if (connection == null) throw new RuntimeException("NATS connection is not initialized")

    try {
      val future: CompletableFuture[Message] =
        connection.request(subject, payload.getBytes(StandardCharsets.UTF_8))

      val msg = future.get(REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS)
      new String(msg.getData, StandardCharsets.UTF_8)
    } catch {
      case e: Exception =>
        throw new RuntimeException(s"NATS request failed for subject: $subject", e)
    }
  }
}
