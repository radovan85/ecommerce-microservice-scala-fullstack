package com.radovan.spring.controllers

import com.radovan.spring.exceptions.{DataNotValidatedException, InstanceUndefinedException, SuspendedUserException}
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.{ExceptionHandler, RestControllerAdvice}
import org.springframework.web.client.HttpClientErrorException

@RestControllerAdvice
class CustomerErrorsController {

  @ExceptionHandler(Array(classOf[DataNotValidatedException]))
  def handleDataNotValidatedException(error: Error): ResponseEntity[String] = {
    new ResponseEntity(error.getMessage, HttpStatus.NOT_ACCEPTABLE)
  }

  @ExceptionHandler(Array(classOf[InstanceUndefinedException]))
  def handleInstanceUndefinedException(error: Error): ResponseEntity[String] = {
    new ResponseEntity(error.getMessage, HttpStatus.UNPROCESSABLE_ENTITY)
  }

  @ExceptionHandler(Array(classOf[HttpClientErrorException]))
  def handleHttpClientErrorException(ex: HttpClientErrorException): ResponseEntity[String] = {
    ResponseEntity.status(ex.getStatusCode).body(ex.getResponseBodyAsString)
  }

  @ExceptionHandler(Array(classOf[SuspendedUserException]))
  def handleSuspendedUserException(error: Error): ResponseEntity[String] = {
    SecurityContextHolder.clearContext()
    new ResponseEntity(error.getMessage, HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS)
  }
}

