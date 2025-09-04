package com.radovan.play.brokers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.radovan.play.utils.NatsUtils
import jakarta.inject.{Inject, Singleton}

@Singleton
class ProductNatsSender @Inject() (
                                    natsUtils: NatsUtils,
                                    objectMapper: ObjectMapper
                                  ) {

  private val ContentType = "application/json"
  private val CartItemsRefreshPrefix = "cart.updateAllByProductId."
  private val CartItemsDeletePrefix  = "cart.removeAllByProductId."

  def sendCartUpdateRequest(productId: Integer, jwtToken: String): Unit = {
    try {
      val messagePayload: ObjectNode = objectMapper.createObjectNode()
      messagePayload.put("Product-ID", productId)
      messagePayload.put("Authorization", jwtToken)

      val subject = CartItemsRefreshPrefix + productId
      val payload = objectMapper.writeValueAsBytes(messagePayload)

      natsUtils.getConnection.publish(subject, payload)
    } catch {
      case e: Exception =>
        e.printStackTrace()
    }
  }

  def sendCartDeleteRequest(productId: Integer, jwtToken: String): Unit = {
    try {
      val messagePayload: ObjectNode = objectMapper.createObjectNode()
      messagePayload.put("Product-ID", productId)
      messagePayload.put("Authorization", jwtToken)

      val subject = CartItemsDeletePrefix + productId
      val payload = objectMapper.writeValueAsBytes(messagePayload)

      natsUtils.getConnection.publish(subject, payload)
    } catch {
      case e: Exception =>
        e.printStackTrace()
    }
  }

}

