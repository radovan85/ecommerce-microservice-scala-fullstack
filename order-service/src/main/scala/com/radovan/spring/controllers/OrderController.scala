package com.radovan.spring.controllers

import java.util.List
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation._
import org.springframework.web.context.request.{RequestAttributes, RequestContextHolder}

import com.radovan.spring.dto.{OrderAddressDto, OrderDto, OrderItemDto}
import com.radovan.spring.services.{OrderAddressService, OrderItemService, OrderService}

@RestController
@RequestMapping(value = Array("/order"))
class OrderController {

  private var orderService: OrderService = _
  private var orderAddressService: OrderAddressService = _
  private var orderItemService: OrderItemService = _

  @Autowired
  private def initialize(orderService: OrderService, orderAddressService: OrderAddressService,
                         orderItemService: OrderItemService): Unit = {
    this.orderService = orderService
    this.orderAddressService = orderAddressService
    this.orderItemService = orderItemService
  }

  private def setAuthorizationHeader(authorizationHeader: String): Unit = {
    RequestContextHolder.getRequestAttributes.setAttribute(
      "Authorization", authorizationHeader, RequestAttributes.SCOPE_REQUEST
    )
  }

  @PreAuthorize(value = "hasAuthority('ROLE_USER')")
  @PostMapping(value = Array("/placeOrder"))
  def placeOrder(
                  @RequestHeader(value = "Authorization", required = false) authorizationHeader: String
                ): ResponseEntity[String] = {
    setAuthorizationHeader(authorizationHeader)
    orderService.addOrder
    new ResponseEntity("Your order has been submitted without any problems.", HttpStatus.OK)
  }

  @PreAuthorize(value = "hasAuthority('ADMIN')")
  @GetMapping(value = Array("/allOrders"))
  def getAllOrders(
                    @RequestHeader(value = "Authorization", required = false) authorizationHeader: String
                  ): ResponseEntity[Array[OrderDto]] = {
    setAuthorizationHeader(authorizationHeader)
    val allOrders = orderService.listAll
    new ResponseEntity(allOrders, HttpStatus.OK)
  }

  @PreAuthorize(value = "hasAuthority('ADMIN')")
  @GetMapping(value = Array("/orderDetails/{orderId}"))
  def orderDetails(
                    @PathVariable("orderId") orderId: Integer,
                    @RequestHeader(value = "Authorization", required = false) authorizationHeader: String
                  ): ResponseEntity[OrderDto] = {
    setAuthorizationHeader(authorizationHeader)
    val order = orderService.getOrderById(orderId)
    new ResponseEntity(order, HttpStatus.OK)
  }

  @PreAuthorize(value = "hasAuthority('ADMIN')")
  @GetMapping(value = Array("/allAddresses"))
  def getAllAddresses(
                       @RequestHeader(value = "Authorization", required = false) authorizationHeader: String
                     ): ResponseEntity[Array[OrderAddressDto]] = {
    setAuthorizationHeader(authorizationHeader)
    val allAddresses = orderAddressService.listAll
    new ResponseEntity(allAddresses, HttpStatus.OK)
  }

  @PreAuthorize(value = "hasAuthority('ADMIN')")
  @GetMapping(value = Array("/allItems/{orderId}"))
  def getAllItems(
                   @PathVariable("orderId") orderId: Integer,
                   @RequestHeader(value = "Authorization", required = false) authorizationHeader: String
                 ): ResponseEntity[Array[OrderItemDto]] = {
    setAuthorizationHeader(authorizationHeader)
    val allItems = orderItemService.listAllByOrderId(orderId)
    new ResponseEntity(allItems, HttpStatus.OK)
  }

  @PreAuthorize(value = "hasAuthority('ADMIN')")
  @DeleteMapping(value = Array("/deleteOrder/{orderId}"))
  def deleteOrder(
                   @PathVariable("orderId") orderId: Integer,
                   @RequestHeader(value = "Authorization", required = false) authorizationHeader: String
                 ): ResponseEntity[String] = {
    setAuthorizationHeader(authorizationHeader)
    orderService.deleteOrder(orderId)
    new ResponseEntity(s"The order with id $orderId has been permanently deleted!", HttpStatus.OK)
  }

  @DeleteMapping(value = Array("/deleteAllByCartId/{cartId}"))
  def deleteAllOrders(
                       @PathVariable("cartId") cartId: Integer,
                       @RequestHeader(value = "Authorization", required = false) authorizationHeader: String,
                       @RequestHeader(value = "X-Source-Service", required = false) sourceService: String
                     ): ResponseEntity[Unit] = {
    if (!sourceService.equals("customer-service")) {
      return new ResponseEntity(HttpStatus.FORBIDDEN) // 403 Forbidden
    }

    setAuthorizationHeader(authorizationHeader)
    orderService.deleteAllByCartId(cartId)
    new ResponseEntity(HttpStatus.OK)
  }
}


