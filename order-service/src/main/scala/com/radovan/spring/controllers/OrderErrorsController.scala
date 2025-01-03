package com.radovan.spring.controllers

import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.{ExceptionHandler, RestControllerAdvice}
import org.springframework.web.client.HttpClientErrorException

import com.radovan.spring.exceptions.{InstanceUndefinedException, OutOfStockException, SuspendedUserException}

@RestControllerAdvice
class OrderErrorsController {

  @ExceptionHandler(Array(classOf[OutOfStockException]))
  def handleOutOfStockException(error: Error): ResponseEntity[String] = {
    new ResponseEntity[String](error.getMessage, HttpStatus.NOT_ACCEPTABLE)
  }

  @ExceptionHandler(Array(classOf[InstanceUndefinedException]))
  def handleInstanceUndefinedException(error: Error): ResponseEntity[String] = {
    new ResponseEntity[String](error.getMessage, HttpStatus.METHOD_NOT_ALLOWED)
  }

  @ExceptionHandler(Array(classOf[HttpClientErrorException]))
  def handleHttpClientErrorException(ex: HttpClientErrorException): ResponseEntity[String] = {
    ResponseEntity.status(ex.getStatusCode).body(ex.getResponseBodyAsString)
  }

  @ExceptionHandler(Array(classOf[SuspendedUserException]))
  def handleSuspendedUserException(error: Error): ResponseEntity[String] = {
    SecurityContextHolder.clearContext()
    new ResponseEntity[String](error.getMessage, HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS)
  }
}

