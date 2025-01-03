package com.radovan.spring.controllers

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation._
import org.springframework.web.context.request.{RequestAttributes, RequestContextHolder}
import com.radovan.spring.dto.CartDto
import com.radovan.spring.services.CartService


@RestController
@RequestMapping(value = Array("/cart"))
class CartController @Autowired()(private val cartService: CartService) {

  private def setAuthorizationHeader(authorizationHeader: String): Unit = {
    RequestContextHolder.getRequestAttributes.setAttribute(
      "Authorization",
      authorizationHeader,
      RequestAttributes.SCOPE_REQUEST
    )
  }

  private val serviceList = List("order-service", "customer-service", "cart-service", "product-service")

  @PostMapping(value = Array("/addCart"))
  def addCart(
               @RequestHeader(value = "X-Source-Service", required = false) sourceService: String
             ): ResponseEntity[CartDto] = {
    if (!sourceService.equals("customer-service")) {
      new ResponseEntity(HttpStatus.FORBIDDEN)
    }
    new ResponseEntity(cartService.addCart, HttpStatus.OK)
  }

  @PreAuthorize(value = "hasAuthority('ROLE_USER')")
  @GetMapping(value = Array("/getMyCart"))
  def getMyCart(
                 @RequestHeader(value = "Authorization", required = false) authorizationHeader: String
               ): ResponseEntity[CartDto] = {
    setAuthorizationHeader(authorizationHeader)
    new ResponseEntity(cartService.getMyCart, HttpStatus.OK)
  }

  @PreAuthorize(value = "hasAuthority('ROLE_USER')")
  @GetMapping(value = Array("/validateCart/{cartId}"))
  def validateCart(
                    @RequestHeader(value = "Authorization", required = false) authorizationHeader: String,
                    @RequestHeader(value = "X-Source-Service", required = false) sourceService: String,
                    @PathVariable("cartId") cartId: Int
                  ): ResponseEntity[CartDto] = {
    if (!sourceService.equals("order-service")) {
      new ResponseEntity(HttpStatus.FORBIDDEN)
    }
    setAuthorizationHeader(authorizationHeader)
    new ResponseEntity(cartService.validateCart(cartId), HttpStatus.OK)
  }

  @PreAuthorize(value = "hasAuthority('ROLE_USER')")
  @GetMapping(value = Array("/calculateCartPrice/{cartId}"))
  def getCartPrice(
                    @RequestHeader(value = "Authorization", required = false) authorizationHeader: String,
                    @PathVariable("cartId") cartId: Int
                  ): ResponseEntity[Float] = {
    setAuthorizationHeader(authorizationHeader)
    new ResponseEntity(cartService.calculateGrandTotal(cartId), HttpStatus.OK)
  }

  @GetMapping(value = Array("/refreshCartState/{cartId}"))
  def refreshCartState(
                        @RequestHeader(value = "Authorization", required = false) authorizationHeader: String,
                        @RequestHeader(value = "X-Source-Service", required = false) sourceService: String,
                        @PathVariable("cartId") cartId: Int
                      ): ResponseEntity[Unit] = {
    if (!serviceList.contains(sourceService)) {
      new ResponseEntity(HttpStatus.FORBIDDEN)
    }
    setAuthorizationHeader(authorizationHeader)
    cartService.refreshCartState(cartId)
    new ResponseEntity(HttpStatus.OK)

  }

  @PutMapping(value = Array("/refreshAllCarts"))
  def refreshAllCarts(
                       @RequestHeader(value = "X-Source-Service", required = false) sourceService: String,
                       @RequestHeader(value = "Authorization", required = false) authorizationHeader: String
                     ): ResponseEntity[Unit] = {
    setAuthorizationHeader(authorizationHeader)
    if (sourceService != "product-service") {
      new ResponseEntity(HttpStatus.FORBIDDEN)
    }
    cartService.refreshAllCarts
    new ResponseEntity(HttpStatus.OK)
  }

  @PreAuthorize(value = "hasAuthority('ROLE_USER')")
  @DeleteMapping(value = Array("/clearCart"))
  def clearMyCart(
                   @RequestHeader(value = "Authorization", required = false) authorizationHeader: String
                 ): ResponseEntity[String] = {
    setAuthorizationHeader(authorizationHeader)
    cartService.clearCart
    new ResponseEntity("All items from your cart have been removed!", HttpStatus.OK)
  }

  @DeleteMapping(value = Array("/deleteCart/{cartId}"))
  def deleteCart(
                  @RequestHeader(value = "Authorization", required = false) authorizationHeader: String,
                  @RequestHeader(value = "X-Source-Service", required = false) sourceService: String,
                  @PathVariable("cartId") cartId: Int
                ): ResponseEntity[Unit] = {
    if (!sourceService.equals("customer-service")) {
      new ResponseEntity(HttpStatus.FORBIDDEN)
    }
    setAuthorizationHeader(authorizationHeader)
    cartService.removeCart(cartId)
    new ResponseEntity(HttpStatus.OK)
  }

}

