package com.radovan.spring.controllers

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.web.bind.annotation._
import org.springframework.web.context.request.{RequestAttributes, RequestContextHolder}

import com.fasterxml.jackson.databind.JsonNode
import com.radovan.spring.services.{OrderAddressService, OrderItemService, OrderService}

@RestController
@RequestMapping(value = Array("/api/order"))
class GatewayOrderController {

  private var orderService: OrderService = _
  private var addressService: OrderAddressService = _
  private var itemService: OrderItemService = _

  @Autowired
  private def initialize(orderService: OrderService, addressService: OrderAddressService, itemService: OrderItemService): Unit = {
    this.orderService = orderService
    this.addressService = addressService
    this.itemService = itemService
  }

  private def setAuthorizationHeader(authorizationHeader: String): Unit = {
    RequestContextHolder.getRequestAttributes.setAttribute("Authorization", authorizationHeader, RequestAttributes.SCOPE_REQUEST)
  }

  @PostMapping(value = Array("/placeOrder"))
  def placeMyOrder(@RequestHeader(value = "Authorization", required = false) authorizationHeader: String): ResponseEntity[String] = {
    setAuthorizationHeader(authorizationHeader)
    new ResponseEntity(orderService.addOrder, HttpStatus.OK)
  }

  @GetMapping(value = Array("/allOrders"))
  def getAllOrders(@RequestHeader(value = "Authorization", required = false) authorizationHeader: String): ResponseEntity[Array[JsonNode]] = {
    setAuthorizationHeader(authorizationHeader)
    new ResponseEntity(orderService.listAll, HttpStatus.OK)
  }

  @GetMapping(value = Array("/orderDetails/{orderId}"))
  def getOrderDetails(
                       @RequestHeader(value = "Authorization", required = false) authorizationHeader: String,
                       @PathVariable("orderId") orderId: Integer): ResponseEntity[JsonNode] = {
    setAuthorizationHeader(authorizationHeader)
    new ResponseEntity(orderService.getOrderById(orderId), HttpStatus.OK)
  }

  @DeleteMapping(value = Array("/deleteOrder/{orderId}"))
  def deleteOrder(
                   @RequestHeader(value = "Authorization", required = false) authorizationHeader: String,
                   @PathVariable("orderId") orderId: Integer): ResponseEntity[String] = {
    setAuthorizationHeader(authorizationHeader)
    new ResponseEntity(orderService.deleteOrder(orderId), HttpStatus.OK)
  }

  @GetMapping(value = Array("/allAddresses"))
  def getAllAddresses(@RequestHeader(value = "Authorization", required = false) authorizationHeader: String): ResponseEntity[Array[JsonNode]] = {
    setAuthorizationHeader(authorizationHeader)
    new ResponseEntity(addressService.listAll, HttpStatus.OK)
  }

  @GetMapping(value = Array("/allItems/{orderId}"))
  def allItemsByOrderId(
                         @RequestHeader(value = "Authorization", required = false) authorizationHeader: String,
                         @PathVariable("orderId") orderId: Integer): ResponseEntity[Array[JsonNode]] = {
    setAuthorizationHeader(authorizationHeader)
    new ResponseEntity(itemService.listAllByOrderId(orderId), HttpStatus.OK)
  }
}

