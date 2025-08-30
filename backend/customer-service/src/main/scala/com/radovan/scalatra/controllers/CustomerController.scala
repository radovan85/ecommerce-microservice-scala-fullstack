package com.radovan.scalatra.controllers

import com.radovan.scalatra.utils.TokenUtils._
import com.fasterxml.jackson.databind.ObjectMapper
import com.radovan.scalatra.dto.CustomerDto
import com.radovan.scalatra.security.{CorsHandler, SecuritySupport}
import com.radovan.scalatra.services.CustomerService
import com.radovan.scalatra.utils.{RegistrationForm, ResponsePackage, ValidatorSupport}
import flexjson.JSONDeserializer
import jakarta.inject.Inject
import org.apache.hc.core5.http.HttpStatus
import org.scalatra.ScalatraServlet

class CustomerController extends ScalatraServlet
  with ValidatorSupport
  with CorsHandler
  with SecuritySupport
  with ErrorsController {

  private var customerService: CustomerService = _
  private var objectMapper: ObjectMapper = _

  @Inject
  private def initialize(customerService: CustomerService, objectMapper: ObjectMapper): Unit = {
    this.customerService = customerService
    this.objectMapper = objectMapper
  }

  get("/") {
    secured(Set("ROLE_ADMIN")) {
      new ResponsePackage[Array[CustomerDto]](customerService.listAll, HttpStatus.SC_OK).toResponse(response)
    }
  }

  get("/:id") {
    secured(Set("ROLE_ADMIN")) {
      val customerId = params("id").toInt
      new ResponsePackage[CustomerDto](customerService.getCustomerById(customerId), HttpStatus.SC_OK).toResponse(response)
    }
  }

  put("/") {
    secured(Set("ROLE_USER")) {
      val json = request.body
      val customer = new JSONDeserializer[CustomerDto]()
        .use(null, classOf[CustomerDto])
        .deserialize(json, classOf[CustomerDto])

      validateOrHalt(customer)
      customerService.updateCustomer(customer, provideToken(request))
      new ResponsePackage[String]("The customer has been updated!", HttpStatus.SC_OK).toResponse(response)
    }
  }


  post("/register") {
    val customerForm = objectMapper.readValue(request.body, classOf[RegistrationForm])

    validateOrHalt(customerForm)

    println("Form has been validated!")
    customerService.addCustomer(customerForm)

    new ResponsePackage[String]("You have been registered successfully", HttpStatus.SC_CREATED).toResponse(response)
  }


  put("/suspend/:id") {
    val customerId = params("id").toInt
    secured(Set("ROLE_ADMIN")) {
      customerService.suspendCustomer(customerId, provideToken(request))
      new ResponsePackage[String](s"Customer $customerId suspended", HttpStatus.SC_OK).toResponse(response)
    }
  }

  put("/reactivate/:id") {
    val customerId = params("id").toInt
    secured(Set("ROLE_ADMIN")) {
      customerService.reactivateCustomer(customerId, provideToken(request))
      new ResponsePackage[String](s"Customer $customerId reactivated", HttpStatus.SC_OK).toResponse(response)
    }
  }

  delete("/:id") {
    val customerId = params("id").toInt
    secured(Set("ROLE_ADMIN")) {
      customerService.removeCustomer(customerId, provideToken(request))
      new ResponsePackage[String](s"Customer $customerId deleted", HttpStatus.SC_OK).toResponse(response)
    }
  }

  get("/getCurrentCustomer") {
    secured(Set("ROLE_USER")) {
      new ResponsePackage[CustomerDto](customerService.getCurrentCustomer(provideToken(request)), HttpStatus.SC_OK).toResponse(response)
    }
  }


}



