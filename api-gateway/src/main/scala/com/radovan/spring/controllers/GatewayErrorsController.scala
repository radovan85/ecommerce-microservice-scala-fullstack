package com.radovan.spring.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.{ExceptionHandler, RestControllerAdvice}
import org.springframework.web.client.HttpClientErrorException

@RestControllerAdvice
class GatewayErrorsController {

  @ExceptionHandler(Array(classOf[HttpClientErrorException]))
  def handleHttpClientErrorException(ex: HttpClientErrorException): ResponseEntity[String] = {
    ResponseEntity.status(ex.getStatusCode).body(ex.getResponseBodyAsString)
  }
}
