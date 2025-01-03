package com.radovan.spring.controllers

import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.{ExceptionHandler, RestControllerAdvice}
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.multipart.MultipartException

import com.radovan.spring.exceptions._

import jakarta.servlet.http.HttpServletRequest

@RestControllerAdvice
class ProductErrorsController {

  @ExceptionHandler(Array(classOf[DataNotValidatedException]))
  def handleDataNotValidatedException(error: Error): ResponseEntity[String] = {
    new ResponseEntity[String](error.getMessage, HttpStatus.NOT_ACCEPTABLE)
  }

  @ExceptionHandler(Array(classOf[InstanceUndefinedException]))
  def handleInstanceUndefinedException(error: Error): ResponseEntity[String] = {
    new ResponseEntity[String](error.getMessage, HttpStatus.UNPROCESSABLE_ENTITY)
  }

  @ExceptionHandler(Array(classOf[HttpClientErrorException]))
  def handleHttpClientErrorException(ex: HttpClientErrorException): ResponseEntity[String] = {
    ResponseEntity.status(ex.getStatusCode).body(ex.getResponseBodyAsString)
  }

  @ExceptionHandler(Array(classOf[FileUploadException]))
  def handleFileUploadException(error: Error): ResponseEntity[String] = {
    new ResponseEntity[String](error.getMessage, HttpStatus.NOT_ACCEPTABLE)
  }

  @ExceptionHandler(Array(classOf[ExistingInstanceException]))
  def handleExistingInstanceException(error: Error): ResponseEntity[String] = {
    new ResponseEntity[String](error.getMessage, HttpStatus.CONFLICT)
  }

  @ExceptionHandler(Array(classOf[MultipartException]))
  def handleMultipartException(request: HttpServletRequest, ex: MultipartException): ResponseEntity[String] = {
    new ResponseEntity[String](s"Error: ${ex.getMessage}", HttpStatus.NOT_ACCEPTABLE)
  }

  @ExceptionHandler(Array(classOf[SuspendedUserException]))
  def handleSuspendedUserException(error: Error): ResponseEntity[String] = {
    SecurityContextHolder.clearContext()
    new ResponseEntity[String](error.getMessage, HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS)
  }
}



