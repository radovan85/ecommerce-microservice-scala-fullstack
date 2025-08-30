package com.radovan.play.brokers

import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.radovan.play.exceptions.InvalidCartException
import com.radovan.play.utils.NatsUtils
import io.nats.client.Message
import jakarta.inject.{Inject, Singleton}

import java.nio.charset.StandardCharsets
import java.util.concurrent.{CompletableFuture, TimeUnit}
import scala.jdk.CollectionConverters._

@Singleton
class OrderNatsSender @Inject()(
                               objectMapper: ObjectMapper,
                               natsUtils: NatsUtils
                               ){

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

  def validateCart(cartId:Int,jwtToken:String):JsonNode = {
    val payload = objectMapper.createObjectNode()
      .put("token", jwtToken)
      .put("cartId", cartId)

    val response = sendRequest(s"cart.validate.$cartId", payload.toString)
    val json = objectMapper.readTree(response)

    val status = Option(json.get("status")).map(_.asInt()).getOrElse(200)
    if (status == 406) {
      val msg = Option(json.get("message")).map(_.asText()).getOrElse("Cart is invalid")
      throw new InvalidCartException(msg)
    } else if (status == 500) {
      val msg = Option(json.get("message")).map(_.asText()).getOrElse("Server error during cart validation")
      throw new RuntimeException(msg)
    }


    json
  }

  def retrieveAddress(addressId:Int,jwtToken:String):JsonNode = {
    val payload = objectMapper.createObjectNode()
      .put("token", jwtToken)
      .put("addressId", addressId)

    val response = sendRequest(s"address.getAddress.$addressId", payload.toString)
    val json = objectMapper.readTree(response)

    if (json.has("status") && json.get("status").asInt() == 500) {
      val msg = if (json.has("error")) json.get("error").asText() else "Failed to retrieve address"
      throw new RuntimeException(msg)
    }

    json
  }

  def updateShippingAddress(address: JsonNode, addressId: Int, jwtToken: String): JsonNode = {
    val payload = objectMapper.createObjectNode()
    payload.set("address", address)
    payload.put("Authorization", jwtToken)

    val subject = s"address.update.$addressId"
    val response = sendRequest(subject, objectMapper.writeValueAsString(payload))
    val json = objectMapper.readTree(response)

    val status = Option(json.get("status")).map(_.asInt()).getOrElse(200)
    if (status == 500) {
      val msg = Option(json.get("message")).map(_.asText()).getOrElse("Address update failed")
      throw new RuntimeException(msg)
    }

    json
  }


  def retrieveCartItems(cartId: Int, jwtToken: String): Array[JsonNode] = {
    val payload = objectMapper.createObjectNode()
      .put("token", jwtToken)
      .put("cartId", cartId)

    val response = sendRequest(s"cart.getItems.$cartId", payload.toString)
    println(s"Response  $response")
    val json = objectMapper.readTree(response)

    if (json.has("status") && json.get("status").asInt() == 500) {
      val msg = if (json.has("error")) json.get("error").asText() else "Failed to retrieve cart items"
      throw new RuntimeException(msg)
    }

    val itemsNode = if (json.isArray) {
      json
    } else if (json.has("items") && json.get("items").isArray) {
      json.get("items")
    } else {
      throw new RuntimeException("Expected array of cart items, but got: " + json.getNodeType)
    }

    itemsNode.elements().asScala.toArray
  }

  def updateProductViaBroker(productNode: ObjectNode, productId: Int, jwtToken: String): JsonNode = {
    val payload = objectMapper.createObjectNode()
    payload.set("product", productNode)
    payload.put("Authorization", jwtToken)

    val subject = s"product.update.$productId"
    val response = sendRequest(subject, objectMapper.writeValueAsString(payload))
    val json = objectMapper.readTree(response)

    if (json.has("status") && json.get("status").asInt() == 500) {
      val msg = if (json.has("message")) json.get("message").asText() else "Product update failed"
      throw new RuntimeException(msg)
    }

    json
  }

  def removeAllByCartId(cartId: Int, jwtToken: String): JsonNode = {
    val payload = objectMapper.createObjectNode()
      .put("Authorization", jwtToken)
      .put("cartId", cartId)

    val response = sendRequest(s"cart.removeAllByCartId.$cartId", payload.toString)
    val json = objectMapper.readTree(response)

    if (json.has("status") && json.get("status").asInt() == 500) {
      val msg = if (json.has("message")) json.get("message").asText() else "Failed to remove items from cart"
      throw new RuntimeException(msg)
    }

    json
  }

  def refreshCartState(cartId: Int, jwtToken: String): JsonNode = {
    val payload = objectMapper.createObjectNode()
      .put("Authorization", jwtToken)
      .put("cartId", cartId)

    val response = sendRequest(s"cart.refreshState.$cartId", payload.toString)
    val json = objectMapper.readTree(response)

    if (json.has("status") && json.get("status").asInt() == 500) {
      val msg = if (json.has("message")) json.get("message").asText() else "Failed to refresh cart state"
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
