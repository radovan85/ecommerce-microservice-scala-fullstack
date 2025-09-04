package com.radovan.spring.brokers

import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.fasterxml.jackson.databind.node.ObjectNode
import com.radovan.spring.converter.DeserializeConverter
import com.radovan.spring.dto.UserDto
import com.radovan.spring.exceptions.ExistingInstanceException
import com.radovan.spring.services.UserService
import com.radovan.spring.utils.{JwtUtil, NatsUtils}
import io.nats.client.{Dispatcher, Message}
import io.nats.client.impl.Headers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

import java.nio.charset.StandardCharsets
import scala.jdk.CollectionConverters._
import scala.util.control.NonFatal

@Component
class UserMessageListener {

  private var natsUtils: NatsUtils = _
  private var userService: UserService = _
  private var objectMapper: ObjectMapper = _
  private var jwtUtil: JwtUtil = _
  private var deserializeConverter: DeserializeConverter = _

  @Autowired
  private def initialize(
                          natsUtils: NatsUtils,
                          userService: UserService,
                          objectMapper: ObjectMapper,
                          jwtUtil: JwtUtil,
                          deserializeConverter: DeserializeConverter
                        ): Unit = {
    this.natsUtils = natsUtils
    this.userService = userService
    this.objectMapper = objectMapper
    this.jwtUtil = jwtUtil
    this.deserializeConverter = deserializeConverter
    initListeners()
  }

  private def initListeners(): Unit = {
    val dispatcher: Dispatcher = natsUtils.getConnection.createDispatcher(handleMessage)
    dispatcher.subscribe("user.get")
    dispatcher.subscribe("user.delete.*")
    dispatcher.subscribe("user.create")
    dispatcher.subscribe("user.suspend.*")
    dispatcher.subscribe("user.reactivate.*")
    dispatcher.subscribe("user.getById.*")
  }

  private def handleMessage(msg: Message): Unit = {
    try {
      msg.getSubject match {
        case "user.get"       => handleUserGet(msg)
        case "user.create"    => handleUserCreate(msg)
        case subject if subject.startsWith("user.getById.") => handleUserGetById(msg)
        case subject if subject.startsWith("user.delete.")    => handleUserDelete(msg)
        case subject if subject.startsWith("user.suspend.")   => handleUserSuspend(msg)
        case subject if subject.startsWith("user.reactivate.")=> handleUserReactivate(msg)
        case _ => // ignore unknown subjects or log if needed
      }
    } catch {
      case NonFatal(_) =>
        sendErrorResponse(msg, "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR)

    }
  }

  @throws[Exception]
  private def handleUserGet(msg: Message): Unit = {
    val request: JsonNode = objectMapper.readTree(msg.getData)
    val token: String = request.get("token").asText()
    authenticateUser(token)

    val currentUser: UserDto = userService.getCurrentUser
    if (currentUser.getEnabled == 0) {
      sendErrorResponse(msg, "Account suspended", HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS)
      return
    }

    natsUtils.getConnection.publish(msg.getReplyTo, objectMapper.writeValueAsBytes(currentUser))
  }

  private def handleUserCreate(msg: Message): Unit = {
    try {
      val userDto: UserDto = deserializeConverter.payloadToUserDto(new String(msg.getData, StandardCharsets.UTF_8))
      val createdUser: UserDto = userService.addUser(userDto)

      val response: ObjectNode = objectMapper.createObjectNode()
      response.put("id", createdUser.getId())
      response.put("status", HttpStatus.OK.value())

      val replyTo = msg.getReplyTo
      if (replyTo != null && !replyTo.isEmpty) {
        natsUtils.getConnection.publish(replyTo, objectMapper.writeValueAsBytes(response)) // ✅ direktan reply
      }

    } catch {
      case _: ExistingInstanceException =>
        sendErrorResponse(msg, "Email already exists", HttpStatus.CONFLICT)
      case NonFatal(_) =>
        sendErrorResponse(msg, "Error creating user", HttpStatus.INTERNAL_SERVER_ERROR)
    }
  }



  private def handleUserDelete(msg: Message): Unit =
    processUserOperation(msg, userId => userService.deleteUser(userId), "User ID %d successfully deleted")

  private def handleUserSuspend(msg: Message): Unit =
    processUserOperation(msg, userId => userService.suspendUser(userId), "User ID %d suspended")

  private def handleUserReactivate(msg: Message): Unit =
    processUserOperation(msg, userId => userService.reactivateUser(userId), "User ID %d reactivated")

  private def processUserOperation(msg: Message, operation: Int => Unit, successMessage: String): Unit = {
    try {
      val userId: Int = extractUserId(msg)
      val replyTo: String = getReplyTo(msg)

      operation(userId)

      val response: ObjectNode = objectMapper.createObjectNode()
      response.put("status", HttpStatus.OK.value())
      response.put("message", successMessage.format(userId))

      natsUtils.getConnection.publish(replyTo, objectMapper.writeValueAsBytes(response))
    } catch {
      case NonFatal(_) =>
        sendErrorResponse(msg, "Error processing operation", HttpStatus.INTERNAL_SERVER_ERROR)
    }
  }

  private def handleUserGetById(msg: Message): Unit = {
    try {
      val userId: Int = msg.getSubject.replace("user.getById.", "").toInt
      val userOpt: Option[UserDto] = Option(userService.getUserById(userId))

      userOpt match {
        case Some(user) =>
          if (user.getEnabled == 0) {
            sendErrorResponse(msg, "Account suspended", HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS)
          } else {
            natsUtils.getConnection.publish(msg.getReplyTo, objectMapper.writeValueAsBytes(user))
          }
        case None =>
          sendErrorResponse(msg, s"User ID $userId not found", HttpStatus.NOT_FOUND)
      }
    } catch {
      case NonFatal(_) =>
        sendErrorResponse(msg, "Error fetching user by ID", HttpStatus.INTERNAL_SERVER_ERROR)
    }
  }


  private def authenticateUser(token: String): Unit = {
    val userId = jwtUtil.extractUsername(token)
    val roles: List[String] = jwtUtil.extractRoles(token)

    val authorities = roles.map(role => new SimpleGrantedAuthority(role)).asJava

    val authentication = new UsernamePasswordAuthenticationToken(userId, token, authorities)
    SecurityContextHolder.getContext.setAuthentication(authentication)
  }

  private def sendErrorResponse(msg: Message, message: String, status: HttpStatus): Unit = {
    val replyTo = msg.getReplyTo
    if (replyTo == null || replyTo.isEmpty) return

    try {
      val errorNode = objectMapper.createObjectNode()
      errorNode.put("status", status.value())
      errorNode.put("message", message)

      natsUtils.getConnection.publish(replyTo, objectMapper.writeValueAsBytes(errorNode)) // ✅ direktan reply
    } catch {
      case _: Throwable => // ignore exceptions while sending error
    }
  }



  private def extractUserId(msg: Message): Int =
    msg.getSubject.replaceAll("user.(delete|suspend|reactivate)\\.", "").toInt

  private def getReplyTo(msg: Message): String = {
    val headers: Headers = msg.getHeaders
    val replyTo = msg.getReplyTo
    if (replyTo == null || replyTo.isEmpty) {
      if (headers != null) headers.getFirst("Nats-Reply-To")
      else "user.response"
    } else replyTo
  }
}

