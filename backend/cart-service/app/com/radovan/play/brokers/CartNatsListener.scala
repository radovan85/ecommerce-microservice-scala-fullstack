package com.radovan.play.brokers

import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.fasterxml.jackson.databind.node.{ArrayNode, ObjectNode}
import com.radovan.play.entity.CartEntity
import com.radovan.play.exceptions.InvalidCartException
import com.radovan.play.repositories.CartRepository
import com.radovan.play.services.{CartItemService, CartService}
import com.radovan.play.utils.NatsUtils
import io.nats.client.MessageHandler
import jakarta.inject.{Inject, Singleton}

import java.time.Instant

@Singleton
class CartNatsListener @Inject()(
                                  natsUtils: NatsUtils,
                                  cartRepository: CartRepository,
                                  objectMapper: ObjectMapper,
                                  cartItemService: CartItemService,
                                  cartService: CartService
                                ) {

  @Inject
  private def initListeners(): Unit = {
    val connection = natsUtils.getConnection
    if (connection != null) {
      val dispatcher = connection.createDispatcher()
      dispatcher.subscribe("cart.create", onCartCreate)
      dispatcher.subscribe("cart.updateAllByProductId.*", onCartUpdate)
      dispatcher.subscribe("cart.delete.*", onCartDelete)
      dispatcher.subscribe("cart.removeAllByProductId.*", onProductDelete)
      dispatcher.subscribe("cart.validate.*", onCartValidate)
      dispatcher.subscribe("cart.getItems.*", getCartItems)
      dispatcher.subscribe("cart.removeAllByCartId.*", onCartClearById)
      dispatcher.subscribe("cart.refreshState.*", onCartRefreshState)

    } else {
      System.err.println("*** NATS connection unavailable — cart.create listener not initialized")
    }
  }

  private val onCartCreate: MessageHandler = msg => {
    try {
      val newCart = new CartEntity()
      val savedCart = cartRepository.save(newCart)

      val response: ObjectNode = objectMapper.createObjectNode()
      response.put("status", 200)
      response.put("id", savedCart.getCartId())

      val replyTo = msg.getReplyTo
      if (replyTo != null) {
        val responseBytes = objectMapper.writeValueAsBytes(response)
        natsUtils.getConnection.publish(replyTo, responseBytes)
      }

    } catch {
      case ex: Exception =>
        val error: ObjectNode = objectMapper.createObjectNode()
        error.put("status", 500)
        error.put("message", s"Cart creation failed: ${ex.getMessage}")
        val replyTo = msg.getReplyTo
        if (replyTo != null) {
          val errorBytes = objectMapper.writeValueAsBytes(error)
          natsUtils.getConnection.publish(replyTo, errorBytes)
        }
    }
  }

  private val onCartUpdate: MessageHandler = msg => {
    try {
      val payload = objectMapper.readTree(msg.getData)
      val productId = extractIdFromSubject(msg.getSubject, "cart.updateAllByProductId.")
      val jwtToken = Option(payload.get("Authorization")).map(_.asText()).getOrElse("")

      cartItemService.updateAllByProductId(productId, jwtToken)

      val response = objectMapper.createObjectNode()
      response.put("status", 200)
      response.put("message", "Cart items updated")
      publishResponse(msg.getReplyTo, response)

    } catch {
      case ex: Exception =>
        val error = objectMapper.createObjectNode()
        error.put("status", 500)
        error.put("message", s"Update failed: ${ex.getMessage}")
        publishResponse(msg.getReplyTo, error)
    }
  }

  private val onCartDelete: MessageHandler = msg => {
    try {
      val payload = objectMapper.readTree(msg.getData)
      val cartId = extractIdFromSubject(msg.getSubject, "cart.delete.")
      val jwtToken = Option(payload.get("Authorization")).map(_.asText()).getOrElse("")

      cartRepository.deleteById(cartId)

      val response = objectMapper.createObjectNode()
      response.put("status", 200)
      response.put("message", "Cart deleted")
      publishResponse(msg.getReplyTo, response)

    } catch {
      case ex: Exception =>
        val error = objectMapper.createObjectNode()
        error.put("status", 500)
        error.put("message", s"Delete failed: ${ex.getMessage}")
        publishResponse(msg.getReplyTo, error)
    }
  }

  private val onCartValidate: MessageHandler = msg => {
    try {
      val cartId = extractIdFromSubject(msg.getSubject, "cart.validate.")
      val payload = objectMapper.readTree(msg.getData)
      val jwtToken = Option(payload.get("Authorization")).map(_.asText()).getOrElse("")

      val cartDto = cartService.validateCart(cartId)
      val responseJson: JsonNode = objectMapper.valueToTree(cartDto)

      publishResponse(msg.getReplyTo, responseJson.asInstanceOf[ObjectNode])

    } catch {
      case ex: InvalidCartException =>
        println("[DEBUG] Caught InvalidCartException directly")
        val error = objectMapper.createObjectNode()
        error.put("status", 406)
        error.put("message", s"Validation failed: ${ex.getMessage}")
        publishResponse(msg.getReplyTo, error)

      case ex: Exception =>
        println(s"[DEBUG] Caught generic exception: ${ex.getClass.getName}")
        val cause = Option(ex.getCause)
        val isInvalidCart = cause.exists(_.isInstanceOf[InvalidCartException])
        val error = objectMapper.createObjectNode()
        error.put("status", if (isInvalidCart) 406 else 500)
        error.put("message", s"Validation failed: ${ex.getMessage}")
        publishResponse(msg.getReplyTo, error)
    }



  }

  private val getCartItems: MessageHandler = msg => {
    try {
      val cartId = extractIdFromSubject(msg.getSubject, "cart.getItems.")
      val payload = objectMapper.readTree(msg.getData)
      val jwtToken = Option(payload.get("Authorization")).map(_.asText()).getOrElse("")

      val cartItemDtos = cartItemService.listAllByCartId(cartId)

      val itemsArrayNode: ArrayNode = objectMapper.createArrayNode()
      cartItemDtos.foreach { dto =>
        val jsonNode: JsonNode = objectMapper.valueToTree(dto)
        itemsArrayNode.add(jsonNode)
      }

      val responseJson: ObjectNode = objectMapper.createObjectNode()
      responseJson.set("items", itemsArrayNode)
      responseJson.put("status", 200)
      responseJson.put("cartId", cartId)
      responseJson.put("timestamp", Instant.now().toString)

      publishResponse(msg.getReplyTo, responseJson)

    } catch {
      case ex: Exception =>
        val errorJson = objectMapper.createObjectNode()
        errorJson.put("status", 500)
        errorJson.put("message", s"Error retrieving items: ${ex.getMessage}")
        publishResponse(msg.getReplyTo, errorJson)
    }
  }


  private val onProductDelete: MessageHandler = msg => {
    try {
      val productId = extractIdFromSubject(msg.getSubject, "cart.removeAllByProductId.")
      val payload = objectMapper.readTree(msg.getData)
      val jwtToken = Option(payload.get("Authorization")).map(_.asText()).getOrElse("")

      cartItemService.removeAllByProductId(productId)

      val response = objectMapper.createObjectNode()
      response.put("status", 200)
      response.put("message", "Product removed from carts")
      publishResponse(msg.getReplyTo, response)

    } catch {
      case ex: Exception =>
        val error = objectMapper.createObjectNode()
        error.put("status", 500)
        error.put("message", s"Remove failed: ${ex.getMessage}")
        publishResponse(msg.getReplyTo, error)
    }
  }

  private val onCartClearById: MessageHandler = msg => {
    try {
      val cartId = extractIdFromSubject(msg.getSubject, "cart.removeAllByCartId.")
      val payload = objectMapper.readTree(msg.getData)
      val jwtToken = Option(payload.get("Authorization")).map(_.asText()).getOrElse("")

      cartItemService.removeAllByCartId(cartId)

      val response = objectMapper.createObjectNode()
      response.put("status", 200)
      response.put("message", s"All items removed from cart $cartId")
      publishResponse(msg.getReplyTo, response)

    } catch {
      case ex: Exception =>
        val error = objectMapper.createObjectNode()
        error.put("status", 500)
        error.put("message", s"Failed to clear cart: ${ex.getMessage}")
        publishResponse(msg.getReplyTo, error)
    }
  }

  private val onCartRefreshState: MessageHandler = msg => {
    try {
      val cartId = extractIdFromSubject(msg.getSubject, "cart.refreshState.")
      val payload = objectMapper.readTree(msg.getData)
      val jwtToken = Option(payload.get("Authorization")).map(_.asText()).getOrElse("")

      cartService.refreshCartState(cartId)

      val response = objectMapper.createObjectNode()
      response.put("status", 200)
      response.put("message", s"Cart state refreshed for cart $cartId")
      publishResponse(msg.getReplyTo, response)

    } catch {
      case ex: Exception =>
        val error = objectMapper.createObjectNode()
        error.put("status", 500)
        error.put("message", s"Failed to refresh cart state: ${ex.getMessage}")
        publishResponse(msg.getReplyTo, error)
    }
  }



  private def publishResponse(replyTo: String, node: ObjectNode): Unit = {
    if (replyTo != null && replyTo.nonEmpty) {
      val bytes = objectMapper.writeValueAsBytes(node)
      natsUtils.getConnection.publish(replyTo, bytes)
    }
  }

  private def extractIdFromSubject(subject: String, prefix: String): Int = {
    val suffix = subject.stripPrefix(prefix)
    try {
      suffix.toInt
    } catch {
      case _: NumberFormatException =>
        throw new IllegalArgumentException(s"Invalid ID in subject: $subject")
    }
  }


}
