package com.radovan.spring.controllers

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation._
import org.springframework.web.context.request.{RequestAttributes, RequestContextHolder}
import com.fasterxml.jackson.databind.JsonNode
import com.radovan.spring.dto.CartItemDto
import com.radovan.spring.services.CartItemService

@RestController
@RequestMapping(value = Array("/items"))
class CartItemController {

  @Autowired
  private var cartItemService: CartItemService = _

  private def setAuthorizationHeader(authorizationHeader: String): Unit = {
    RequestContextHolder.getRequestAttributes.setAttribute(
      "Authorization",
      authorizationHeader,
      RequestAttributes.SCOPE_REQUEST
    )
  }

  private val serviceList: List[String] = List("order-service", "customer-service", "cart-service", "product-service")

  @PreAuthorize(value = "hasAuthority('ROLE_USER')")
  @PostMapping(value = Array("/addItem/{productId}"))
  def addCartItem(
                   @RequestHeader(value = "Authorization", required = false) authorizationHeader: String,
                   @PathVariable("productId") productId: Int
                 ): ResponseEntity[String] = {
    setAuthorizationHeader(authorizationHeader)
    cartItemService.addCartItem(productId)
    new ResponseEntity("The item has been added to the cart!", HttpStatus.OK)
  }

  @PreAuthorize(value = "hasAuthority('ROLE_USER')")
  @DeleteMapping(value = Array("/deleteItem/{itemId}"))
  def deleteItem(
                  @RequestHeader(value = "Authorization", required = false) authorizationHeader: String,
                  @PathVariable("itemId") itemId: Int
                ): ResponseEntity[String] = {
    setAuthorizationHeader(authorizationHeader)
    cartItemService.removeCartItem(itemId)
    new ResponseEntity("The item has been removed from the cart!", HttpStatus.OK)
  }

  @PreAuthorize(value = "hasAuthority('ADMIN')")
  @DeleteMapping(value = Array("/clearAllByProductId/{productId}"))
  def deleteAllByProductId(
                            @RequestHeader(value = "Authorization", required = false) authorizationHeader: String,
                            @PathVariable("productId") productId: Int
                          ): ResponseEntity[Unit] = {
    setAuthorizationHeader(authorizationHeader)
    cartItemService.removeAllByProductId(productId)
    new ResponseEntity(HttpStatus.OK)
  }

  @PreAuthorize(value = "hasAuthority('ADMIN')")
  @DeleteMapping(value = Array("/clearAllByCartId/{cartId}"))
  def deleteAllByCartId(
                         @RequestHeader(value = "Authorization", required = false) authorizationHeader: String,
                         @PathVariable("cartId") cartId: Int
                       ): ResponseEntity[Unit] = {
    setAuthorizationHeader(authorizationHeader)
    cartItemService.removeAllByCartId(cartId)
    new ResponseEntity(HttpStatus.OK)
  }

  @GetMapping(value = Array("/allItemsByCartId/{cartId}"))
  def findAllItemsByCartId(
                            @RequestHeader(value = "Authorization", required = false) authorizationHeader: String,
                            @PathVariable("cartId") cartId: Int
                          ): ResponseEntity[Array[CartItemDto]] = {
    setAuthorizationHeader(authorizationHeader)
    val items = cartItemService.listAllByCartId(cartId)
    new ResponseEntity(items, HttpStatus.OK)
  }

  @GetMapping(value = Array("/allItemsByProductId/{productId}"))
  def findAllItemsByProductId(
                               @RequestHeader(value = "Authorization", required = false) authorizationHeader: String,
                               @PathVariable("productId") productId: Int
                             ): ResponseEntity[Array[CartItemDto]] = {
    setAuthorizationHeader(authorizationHeader)
    val items = cartItemService.listAllByProductId(productId)
    new ResponseEntity(items, HttpStatus.OK)
  }

  @GetMapping(value = Array("/itemDetails/{itemId}"))
  def getItemDetails(
                      @RequestHeader(value = "Authorization", required = false) authorizationHeader: String,
                      @RequestHeader(value = "X-Source-Service", required = false) sourceService: String,
                      @PathVariable("itemId") itemId: Int
                    ): ResponseEntity[CartItemDto] = {
    if (!serviceList.contains(sourceService)) {
      new ResponseEntity(HttpStatus.FORBIDDEN)
    }
    setAuthorizationHeader(authorizationHeader)
    val item = cartItemService.getItemById(itemId)
    new ResponseEntity(item, HttpStatus.OK)

  }

  @PutMapping(value = Array("/updateItem/{itemId}"))
  def updateItem(
                  @RequestHeader(value = "X-Source-Service", required = false) sourceService: String,
                  @RequestHeader(value = "Authorization", required = false) authorizationHeader: String,
                  @PathVariable("itemId") itemId: Int,
                  @RequestBody item: JsonNode
                ): ResponseEntity[Unit] = {
    if (!"product-service".equals(sourceService)) {
      new ResponseEntity(HttpStatus.FORBIDDEN)
    }
    setAuthorizationHeader(authorizationHeader)
    cartItemService.updateItem(itemId, item)
    new ResponseEntity(HttpStatus.OK)
  }

}
