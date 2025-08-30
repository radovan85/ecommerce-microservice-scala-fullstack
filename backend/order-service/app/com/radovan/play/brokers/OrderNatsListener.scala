package com.radovan.play.brokers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.radovan.play.services.OrderService
import com.radovan.play.utils.NatsUtils
import io.nats.client.{Message, MessageHandler}
import io.nats.client.impl.Headers
import jakarta.inject.{Inject, Singleton}

import scala.util.Try

@Singleton
class OrderNatsListener @Inject()(
                                 natsUtils: NatsUtils,
                                 objectMapper: ObjectMapper,
                                 orderService: OrderService
                                 ){

  @Inject
  private def initListeners(): Unit = {
    val connection = natsUtils.getConnection
    if (connection != null) {
      val dispatcher = connection.createDispatcher()
      dispatcher.subscribe("order.deleteAll.*", onOrdersDelete)
    } else {
      System.err.println("*** NATS connection unavailable — order listener not initialized")
    }
  }

  private val onOrdersDelete: MessageHandler = (msg: Message) => {
    val subject = msg.getSubject
    val replyTo = msg.getReplyTo
    val cartId = extractIdFromSubject(subject, "order.deleteAll.")

    try {
      orderService.deleteAllByCartId(cartId)

      val responseNode = objectMapper.createObjectNode()
      responseNode.put("status", 200)
      responseNode.put("message", s"All orders for cart ID $cartId deleted successfully")

      publishResponse(replyTo, responseNode)
    } catch {
      case _: Exception =>
        sendErrorResponse(replyTo, "Unexpected error while deleting orders", 500)
    }
  }





  private def extractIdFromSubject(subject: String, prefix: String): Int = {
    Try(subject.replace(prefix, "").toInt).getOrElse(0)
  }

  private def publishResponse(replyTo: String, node: ObjectNode): Unit = {
    if (replyTo != null && replyTo.nonEmpty) {
      val bytes = objectMapper.writeValueAsBytes(node)
      natsUtils.getConnection.publish(replyTo, bytes)
    }
  }

  private def sendErrorResponse(replyTo: String, message: String, status: Int): Unit = {
    try {
      val errorNode: ObjectNode = objectMapper.createObjectNode()
      errorNode.put("status", status)
      errorNode.put("message", message)

      val headers = new Headers()
      headers.add("Content-Type", "application/json")

      println(s"ERROR: $message")
      natsUtils.getConnection.publish(replyTo, headers, objectMapper.writeValueAsBytes(errorNode))
    } catch {
      case _: Exception =>
        natsUtils.getConnection.publish(replyTo, Array.emptyByteArray)
    }
  }
}
