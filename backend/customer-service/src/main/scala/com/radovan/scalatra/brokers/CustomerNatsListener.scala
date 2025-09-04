package com.radovan.scalatra.brokers

import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.radovan.scalatra.dto.ShippingAddressDto
import com.radovan.scalatra.services.{CustomerService, ShippingAddressService}
import com.radovan.scalatra.utils.NatsUtils
import io.nats.client.{Dispatcher, MessageHandler}
import jakarta.inject.{Inject, Singleton}
import org.slf4j.LoggerFactory

import scala.util.Try

@Singleton
class CustomerNatsListener @Inject()(
                                      natsUtils: NatsUtils,
                                      objectMapper: ObjectMapper,
                                      customerService: CustomerService,
                                      addressService: ShippingAddressService
                                    ) {

  private val logger = LoggerFactory.getLogger(getClass)
  private val connection = natsUtils.getConnection

  @Inject
  private def init(): Unit = {
    val dispatcher: Dispatcher = connection.createDispatcher()
    dispatcher.subscribe("customer.getCurrent", onGetCurrentCustomer)
    dispatcher.subscribe("address.getAddress.*", getAddressNode)
    dispatcher.subscribe("address.update.*", onAddressUpdate)
    logger.info("*** Subscribed to customer.getCurrent")
  }



  private val onGetCurrentCustomer: MessageHandler = msg => {
    try {
      val payload: JsonNode = objectMapper.readTree(msg.getData)
      val jwtToken = Option(payload.get("token")).map(_.asText()).getOrElse {
        throw new RuntimeException("Missing token in customer.getCurrent payload")
      }

      val currentUserNode = customerService.getCurrentCustomer(jwtToken)

      val replyTo = Option(msg.getReplyTo).getOrElse("customer.response")
      connection.publish(replyTo, objectMapper.writeValueAsBytes(currentUserNode))

    } catch {
      case ex: Exception =>
        val errorNode: ObjectNode = objectMapper.createObjectNode()
        errorNode.put("status", 500)
        errorNode.put("error", s"Failed to retrieve current customer: ${ex.getMessage}")
        val replyTo = Option(msg.getReplyTo).getOrElse("customer.response")
        connection.publish(replyTo, objectMapper.writeValueAsBytes(errorNode))

    }
  }

  private val getAddressNode: MessageHandler = msg => {
    try {
      val payload: JsonNode = objectMapper.readTree(msg.getData)
      val jwtToken = Option(payload.get("token")).map(_.asText()).getOrElse {
        throw new RuntimeException("Missing token in address.getAddress payload")
      }
      val addressId = extractIdFromSubject(msg.getSubject, "address.getAddress.")

      val addressNode = addressService.getAddressById(addressId)

      val replyTo = Option(msg.getReplyTo).getOrElse("address.response")
      connection.publish(replyTo, objectMapper.writeValueAsBytes(addressNode))

    } catch {
      case ex: Exception =>
        val errorNode: ObjectNode = objectMapper.createObjectNode()
        errorNode.put("status", 500)
        errorNode.put("error", s"Failed to retrieve address: ${ex.getMessage}")
        val replyTo = Option(msg.getReplyTo).getOrElse("address.response")
        connection.publish(replyTo, objectMapper.writeValueAsBytes(errorNode))

    }
  }

  private val onAddressUpdate: MessageHandler = msg => {
    try {
      val addressId = extractIdFromSubject(msg.getSubject, "address.update.")
      val payload: JsonNode = objectMapper.readTree(msg.getData)

      val jwtToken = Option(payload.get("Authorization")).map(_.asText()).getOrElse {
        throw new RuntimeException("Missing Authorization token in address.update payload")
      }

      val addressNode = Option(payload.get("address")).getOrElse {
        throw new RuntimeException("Missing address node in payload")
      }

      // 🔄 Parsiranje u ShippingAddressDto
      val dto = objectMapper.treeToValue(addressNode, classOf[ShippingAddressDto])
      dto.setShippingAddressId(addressId)

      val updatedAddress = addressService.updateAddress(dto, addressId)

      val responseNode = objectMapper.createObjectNode()
      responseNode.put("status", 200)
      responseNode.set("address", objectMapper.valueToTree(updatedAddress))

      val replyTo = Option(msg.getReplyTo).getOrElse("address.response")
      connection.publish(replyTo, objectMapper.writeValueAsBytes(responseNode))

    } catch {
      case ex: Exception =>
        val errorNode = objectMapper.createObjectNode()
        errorNode.put("status", 500)
        errorNode.put("message", s"Address update failed: ${ex.getMessage}")
        val replyTo = Option(msg.getReplyTo).getOrElse("address.response")
        connection.publish(replyTo, objectMapper.writeValueAsBytes(errorNode))
    }
  }



  private def extractIdFromSubject(subject: String, prefix: String): Int = {
    Try(subject.replace(prefix, "").toInt).getOrElse(0)
  }


}

