package com.radovan.spring.controllers

import com.radovan.spring.dto.CustomerDto
import com.radovan.spring.exceptions.DataNotValidatedException
import com.radovan.spring.services.CustomerService
import com.radovan.spring.utils.RegistrationForm
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.Errors
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation._
import org.springframework.web.context.request.{RequestAttributes, RequestContextHolder}

@RestController
@RequestMapping(Array("/customers"))
class CustomerController {

  @Autowired
  private var customerService: CustomerService = _

  private def setAuthorizationHeader(authorizationHeader: String): Unit = {
    RequestContextHolder.getRequestAttributes.setAttribute(
      "Authorization",
      authorizationHeader,
      RequestAttributes.SCOPE_REQUEST
    )
  }

  @PostMapping(Array("/createCustomer"))
  def createCustomer(
                      @Validated @RequestBody form: RegistrationForm,
                      errors: Errors,
                      @RequestHeader(value = "Authorization", required = false) authorizationHeader: String
                    ): ResponseEntity[CustomerDto] = {
    if (authorizationHeader != null) {
      new ResponseEntity(HttpStatus.FORBIDDEN)
    }
    if (errors.hasErrors) {
      throw new DataNotValidatedException(new Error("The data has not been validated!"))
    }

    new ResponseEntity(customerService.addCustomer(form), HttpStatus.OK)

  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @GetMapping(Array("/allCustomers"))
  def getAllCustomers(
                       @RequestHeader(value = "Authorization", required = false) authorizationHeader: String
                     ): ResponseEntity[Array[CustomerDto]] = {
    setAuthorizationHeader(authorizationHeader)
    new ResponseEntity(customerService.listAll, HttpStatus.OK)
  }

  @PreAuthorize("hasAuthority('ROLE_USER')")
  @GetMapping(Array("/currentCustomer"))
  def getCurrentCustomer(
                          @RequestHeader(value = "Authorization", required = false) authorizationHeader: String
                        ): ResponseEntity[CustomerDto] = {
    setAuthorizationHeader(authorizationHeader)
    new ResponseEntity(customerService.getCurrentCustomer, HttpStatus.OK)
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @DeleteMapping(Array("/deleteCustomer/{customerId}"))
  def deleteCustomer(
                      @RequestHeader(value = "Authorization", required = false) authorizationHeader: String,
                      @PathVariable("customerId") customerId: Integer
                    ): ResponseEntity[String] = {
    setAuthorizationHeader(authorizationHeader)
    customerService.removeCustomer(customerId)
    new ResponseEntity(
      s"The customer with id: $customerId has been permanently deleted!",
      HttpStatus.OK
    )
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @PutMapping(Array("/suspendCustomer/{customerId}"))
  def suspendCustomer(
                       @RequestHeader(value = "Authorization", required = false) authorizationHeader: String,
                       @PathVariable("customerId") customerId: Integer
                     ): ResponseEntity[String] = {
    setAuthorizationHeader(authorizationHeader)
    customerService.suspendCustomer(customerId)
    new ResponseEntity(s"The customer with id $customerId has been suspended", HttpStatus.OK)
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @PutMapping(Array("/reactivateCustomer/{customerId}"))
  def reactivateCustomer(
                          @RequestHeader(value = "Authorization", required = false) authorizationHeader: String,
                          @PathVariable("customerId") customerId: Integer
                        ): ResponseEntity[String] = {
    setAuthorizationHeader(authorizationHeader)
    customerService.reactivateCustomer(customerId)
    new ResponseEntity(
      s"The customer with id $customerId has been reactivated!",
      HttpStatus.OK
    )
  }
}

