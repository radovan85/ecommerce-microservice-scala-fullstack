package com.radovan.spring.controllers

import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.{ControllerAdvice, ExceptionHandler}
import org.springframework.web.client.HttpClientErrorException

import com.radovan.spring.exceptions._

@ControllerAdvice
class CartErrorsController {

  @ExceptionHandler(Array(classOf[InstanceUndefinedException]))
  def handleInstanceUndefinedException(error: Error): ResponseEntity[String] = {
    new ResponseEntity[String](error.getMessage, HttpStatus.UNPROCESSABLE_ENTITY)
  }

  @ExceptionHandler(Array(classOf[HttpClientErrorException]))
  def handleHttpClientErrorException(ex: HttpClientErrorException): ResponseEntity[String] = {
    ResponseEntity.status(ex.getStatusCode).body(ex.getResponseBodyAsString)
  }

  @ExceptionHandler(Array(classOf[InvalidCartException]))
  def handleInvalidCartException(error: Error): ResponseEntity[String] = {
    new ResponseEntity[String](error.getMessage, HttpStatus.NOT_ACCEPTABLE)
  }

  @ExceptionHandler(Array(classOf[OperationNotAllowedException]))
  def handleOperationNotAllowedException(error: Error): ResponseEntity[String] = {
    new ResponseEntity[String](error.getMessage, HttpStatus.NON_AUTHORITATIVE_INFORMATION)
  }

  @ExceptionHandler(Array(classOf[SuspendedUserException]))
  def handleSuspendedUserException(error: Error): ResponseEntity[String] = {
    SecurityContextHolder.clearContext()
    new ResponseEntity[String](error.getMessage, HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS)
  }

  @ExceptionHandler(Array(classOf[OutOfStockException]))
  def handleOutOfStockException(error: Error): ResponseEntity[String] = {
    new ResponseEntity[String](error.getMessage, HttpStatus.NOT_ACCEPTABLE)
  }
}

