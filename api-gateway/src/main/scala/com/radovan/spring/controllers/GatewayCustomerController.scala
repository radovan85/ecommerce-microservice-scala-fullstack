package com.radovan.spring.controllers

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.web.bind.annotation._
import org.springframework.web.context.request.{RequestAttributes, RequestContextHolder}
import com.fasterxml.jackson.databind.JsonNode
import com.radovan.spring.services.{CustomerService, ShippingAddressService}

@RestController
@RequestMapping(Array("/api/customers"))
class GatewayCustomerController {

  private var customerService: CustomerService = _
  private var addressService: ShippingAddressService = _

  @Autowired
  private def initialize(customerService: CustomerService, addressService: ShippingAddressService): Unit = {
    this.customerService = customerService
    this.addressService = addressService
  }

  private def setAuthorizationHeader(authorizationHeader: String): Unit = {
    RequestContextHolder.getRequestAttributes.setAttribute(
      "Authorization",
      authorizationHeader,
      RequestAttributes.SCOPE_REQUEST
    )
  }

  @PostMapping(value = Array("/createCustomer"))
  def createCustomer(
                      @RequestHeader(value = "Authorization", required = false) authorizationHeader: String,
                      @RequestBody form: JsonNode
                    ): ResponseEntity[JsonNode] = {
    new ResponseEntity(customerService.addCustomer(form), HttpStatus.OK)
  }

  @GetMapping(value = Array("/allCustomers"))
  def getAllCustomers(
                       @RequestHeader(value = "Authorization", required = false) authorizationHeader: String
                     ): ResponseEntity[Array[JsonNode]] = {
    setAuthorizationHeader(authorizationHeader)
    val customers = customerService.listAll
    new ResponseEntity(customers, HttpStatus.OK)
  }

  @DeleteMapping(value = Array("/deleteCustomer/{customerId}"))
  def deleteCustomer(
                      @RequestHeader(value = "Authorization", required = false) authorizationHeader: String,
                      @PathVariable("customerId") customerId: Integer
                    ): ResponseEntity[String] = {
    setAuthorizationHeader(authorizationHeader)
    new ResponseEntity(customerService.deleteCustomer(customerId), HttpStatus.OK)
  }

  @PutMapping(value = Array("/updateAddress"))
  def updateAddress(
                     @RequestHeader(value = "Authorization", required = false) authorizationHeader: String,
                     @RequestBody address: JsonNode
                   ): ResponseEntity[String] = {
    setAuthorizationHeader(authorizationHeader)
    new ResponseEntity(addressService.updateAddress(address), HttpStatus.OK)
  }

  @PutMapping(value = Array("/suspendCustomer/{customerId}"))
  def suspendCustomer(
                       @RequestHeader(value = "Authorization", required = false) authorizationHeader: String,
                       @PathVariable("customerId") customerId: Integer
                     ): ResponseEntity[String] = {
    setAuthorizationHeader(authorizationHeader)
    new ResponseEntity(customerService.suspendCustomer(customerId), HttpStatus.OK)
  }

  @RequestMapping(value = Array("/reactivateCustomer/{customerId}"))
  def reactivateCustomer(
                          @RequestHeader(value = "Authorization", required = false) authorizationHeader: String,
                          @PathVariable("customerId") customerId: Integer
                        ): ResponseEntity[String] = {
    setAuthorizationHeader(authorizationHeader)
    new ResponseEntity(customerService.reactivateCustomer(customerId), HttpStatus.OK)
  }
}
