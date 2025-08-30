package com.radovan.spring.controllers

import com.radovan.spring.exceptions.{DataNotValidatedException, ExistingInstanceException, InstanceUndefinedException, OperationNotAllowedException, SuspendedUserException}
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.web.bind.annotation.{ExceptionHandler, RestControllerAdvice}

import javax.security.auth.login.CredentialNotFoundException

@RestControllerAdvice
class ErrorsController {

  @ExceptionHandler(Array(classOf[DataNotValidatedException]))
  def handleDataNotValidatedException(error: Error) = new ResponseEntity[String](error.getMessage, HttpStatus.NOT_ACCEPTABLE)

  @ExceptionHandler(Array(classOf[ExistingInstanceException]))
  def handleExistingInstanceException(error: Error) = new ResponseEntity[String](error.getMessage, HttpStatus.CONFLICT)

  @ExceptionHandler(Array(classOf[InstanceUndefinedException]))
  def handleInstanceUndefinedException(error: Error) = new ResponseEntity[String](error.getMessage, HttpStatus.UNPROCESSABLE_ENTITY)

  @ExceptionHandler(Array(classOf[SuspendedUserException]))
  def handleSuspendedUserException(error: Error) = new ResponseEntity[String](error.getMessage, HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS)

  @ExceptionHandler(Array(classOf[CredentialNotFoundException]))
  def handleCredentialsNotFoundException(exc: CredentialNotFoundException) = new ResponseEntity[String](exc.getMessage, HttpStatus.NOT_ACCEPTABLE)

  @ExceptionHandler(Array(classOf[OperationNotAllowedException]))
  def handleOperationNotAllowedException(error:Error) = new ResponseEntity[String](error.getMessage,HttpStatus.NOT_ACCEPTABLE)
}