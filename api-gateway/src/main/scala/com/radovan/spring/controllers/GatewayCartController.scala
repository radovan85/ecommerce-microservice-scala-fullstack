package com.radovan.spring.controllers

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.web.bind.annotation._
import org.springframework.web.context.request.{RequestAttributes, RequestContextHolder}
import com.fasterxml.jackson.databind.JsonNode
import com.radovan.spring.services.{CartItemService, CartService}

@RestController
@RequestMapping(value = Array("/api/cart"))
class GatewayCartController {

  private var cartService: CartService = _
  private var cartItemService: CartItemService = _

  @Autowired
  private def initialize(cartService: CartService, cartItemService: CartItemService): Unit = {
    this.cartService = cartService
    this.cartItemService = cartItemService
  }

  private def setAuthorizationHeader(authorizationHeader: String): Unit = {
    RequestContextHolder.getRequestAttributes.setAttribute(
      "Authorization",
      authorizationHeader,
      RequestAttributes.SCOPE_REQUEST
    )
  }

  @GetMapping(value = Array("/getMyCart"))
  def getMyCart(
                 @RequestHeader(value = "Authorization", required = false) authorizationHeader: String
               ): ResponseEntity[JsonNode] = {
    setAuthorizationHeader(authorizationHeader)
    new ResponseEntity(cartService.getMyCart, HttpStatus.OK)
  }

  @DeleteMapping(value = Array("/clearCart"))
  def clearMyCart(
                   @RequestHeader(value = "Authorization", required = false) authorizationHeader: String
                 ): ResponseEntity[String] = {
    setAuthorizationHeader(authorizationHeader)
    new ResponseEntity(cartService.clearCart, HttpStatus.OK)
  }

  @PostMapping(value = Array("/addItem/{productId}"))
  def addCartItem(
                   @RequestHeader(value = "Authorization", required = false) authorizationHeader: String,
                   @PathVariable("productId") productId: Integer
                 ): ResponseEntity[String] = {
    setAuthorizationHeader(authorizationHeader)
    new ResponseEntity(cartItemService.addCartItem(productId), HttpStatus.OK)
  }

  @DeleteMapping(value = Array("/deleteItem/{itemId}"))
  def deleteItem(
                  @RequestHeader(value = "Authorization", required = false) authorizationHeader: String,
                  @PathVariable("itemId") itemId: Integer
                ): ResponseEntity[String] = {
    setAuthorizationHeader(authorizationHeader)
    new ResponseEntity(cartItemService.deleteItem(itemId), HttpStatus.OK)
  }
}

