package com.radovan.play.brokers

import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.fasterxml.jackson.databind.node.ObjectNode
import com.radovan.play.dto.ProductDto
import com.radovan.play.exceptions.InstanceUndefinedException
import com.radovan.play.services.ProductService
import com.radovan.play.utils.NatsUtils
import io.nats.client.{Connection, Dispatcher, Message}
import io.nats.client.impl.Headers
import jakarta.inject.{Inject, Singleton}

import scala.util.Try

@Singleton
class ProductNatsListener @Inject() (
                                      productService: ProductService,
                                      natsUtils: NatsUtils,
                                      objectMapper: ObjectMapper
                                    ) {

  private val ProductIdHeader     = "Product-ID"
  private val ContentTypeHeader   = "Content-Type"
  private val ApplicationJson     = "application/json"
  private val ProductUpdatePrefix = "product.update."
  private val ProductGetPrefix    = "product.get."

  @Inject
  private def initListeners(): Unit = {
    try {
      val connection: Connection = natsUtils.getConnection
      val dispatcher: Dispatcher = connection.createDispatcher(handleMessage)

      dispatcher.subscribe("product.update.*")
      dispatcher.subscribe("product.get.*")
    } catch {
      case e: Exception =>
        System.err.println(s"PRODUCT SERVICE INIT ERROR: ${e.getMessage}")
        throw new RuntimeException("NATS initialization failed", e)
    }
  }

  private def handleMessage(msg: Message): Unit = {
    try {
      val subject = msg.getSubject
      if (subject.startsWith(ProductUpdatePrefix)) {
        handleUpdateRequest(msg)
      } else if (subject.startsWith(ProductGetPrefix)) {
        handleGetRequest(msg)
      }
    } catch {
      case e: Exception =>
        System.err.println(s"PRODUCT SERVICE ERROR: ${e.getMessage}")
        sendErrorResponse(msg.getReplyTo, "Internal server error", 500)
    }
  }

  private def handleGetRequest(msg: Message): Unit = {
    val productId = extractIdFromSubject(msg.getSubject, ProductGetPrefix)
    try {
      val product = productService.getProductById(productId)
      val response: ObjectNode = objectMapper.createObjectNode()
      response.set("product", objectMapper.valueToTree(product))
      natsUtils.getConnection.publish(msg.getReplyTo, objectMapper.writeValueAsBytes(response))
    } catch {
      case _: Exception =>
        sendErrorResponse(msg.getReplyTo, "Failed to retrieve product", 500)
    }
  }

  private def handleUpdateRequest(msg: Message): Unit = {
    try {
      val productId = extractIdFromSubject(msg.getSubject, ProductUpdatePrefix)
      val payload: JsonNode = objectMapper.readTree(msg.getData)

      if (!payload.has("product")) throw new RuntimeException("Missing 'product' field")
      if (!payload.has("Authorization")) throw new RuntimeException("Missing Authorization token")

      val jwtToken = payload.get("Authorization").asText()
      val productDto = objectMapper.treeToValue(payload.get("product"), classOf[ProductDto])

      val updatedProduct = productService.updateProduct(productDto, productId, jwtToken)

      val response: ObjectNode = objectMapper.createObjectNode()
      response.set("product", objectMapper.valueToTree(updatedProduct))
      natsUtils.getConnection.publish(msg.getReplyTo, objectMapper.writeValueAsBytes(response))

    } catch {
      case ex: Exception =>
        sendErrorResponse(msg.getReplyTo, s"Update failed: ${ex.getMessage}", 500)
    }
  }

  private def extractIdFromSubject(subject: String, prefix: String): Int = {
    Try(subject.replace(prefix, "").toInt).getOrElse(0)
  }

  private def sendErrorResponse(replyTo: String, message: String, status: Int): Unit = {
    try {
      val errorNode: ObjectNode = objectMapper.createObjectNode()
      errorNode.put("status", status)
      errorNode.put("message", message)

      val headers = new Headers()
      headers.add(ContentTypeHeader, ApplicationJson)

      println(s"ERROR: $message")
      natsUtils.getConnection.publish(replyTo, headers, objectMapper.writeValueAsBytes(errorNode))
    } catch {
      case _: Exception =>
        natsUtils.getConnection.publish(replyTo, Array.emptyByteArray)
    }
  }
}
