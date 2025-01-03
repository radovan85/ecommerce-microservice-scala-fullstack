package com.radovan.spring.controllers

import com.fasterxml.jackson.databind.JsonNode
import com.radovan.spring.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.web.bind.annotation._
import org.springframework.web.context.request.{RequestAttributes, RequestContextHolder}

@RestController
@RequestMapping(value = Array("/api/users"))
class GatewayUserController {

  @Autowired
  private var userService: UserService = _

  private def setAuthorizationHeader(authorizationHeader: String): Unit = {
    RequestContextHolder.getRequestAttributes.setAttribute("Authorization", authorizationHeader, RequestAttributes.SCOPE_REQUEST)
  }

  @PostMapping(value = Array("/login"))
  def createAuthenticationToken(
                                 @RequestBody authRequest: JsonNode,
                                 @RequestHeader(value = "Authorization", required = false) authorizationHeader: String): ResponseEntity[JsonNode] = {

    if (authorizationHeader != null) {
      return new ResponseEntity(HttpStatus.FORBIDDEN)
    }

    new ResponseEntity(userService.authenticateUser(authRequest), HttpStatus.OK)
  }

  @GetMapping(value = Array("/allUsers"))
  def getAllUsers(
                   @RequestHeader(value = "Authorization", required = false) authorizationHeader: String): ResponseEntity[Array[JsonNode]] = {
    setAuthorizationHeader(authorizationHeader)
    new ResponseEntity(userService.listAll, HttpStatus.OK)
  }

  @GetMapping(value = Array("/currentUser"))
  def getCurrentUser(
                      @RequestHeader(value = "Authorization", required = false) authorizationHeader: String): ResponseEntity[JsonNode] = {
    setAuthorizationHeader(authorizationHeader)
    new ResponseEntity(userService.getCurrentUser, HttpStatus.OK)
  }

  @GetMapping(value = Array("/userDetails/{userId}"))
  def getUserDetails(
                      @RequestHeader(value = "Authorization", required = false) authorizationHeader: String,
                      @PathVariable("userId") userId: Integer): ResponseEntity[JsonNode] = {
    setAuthorizationHeader(authorizationHeader)
    new ResponseEntity(userService.getUserById(userId), HttpStatus.OK)
  }
}

