package com.radovan.scalatra.controllers

import com.radovan.scalatra.exceptions.{DataNotValidatedException, ExistingInstanceException, ForbiddenAccessException, InstanceUndefinedException, SuspendedUserException}
import com.radovan.scalatra.utils.ResponsePackage
import org.apache.hc.core5.http.HttpStatus
import org.scalatra._

trait ErrorsController extends ScalatraBase {

  error {
    case e: InstanceUndefinedException =>
      new ResponsePackage[String](e.getMessage, HttpStatus.SC_PRECONDITION_FAILED).toResponse(response)

    case e: ExistingInstanceException =>
      new ResponsePackage[String](e.getMessage, HttpStatus.SC_CONFLICT).toResponse(response)

    case e: DataNotValidatedException =>
      new ResponsePackage[String](e.getMessage, HttpStatus.SC_BAD_REQUEST).toResponse(response)

    case e: SuspendedUserException =>
      new ResponsePackage[String](e.getMessage, HttpStatus.SC_UNAVAILABLE_FOR_LEGAL_REASONS).toResponse(response)

    case e: ForbiddenAccessException =>
      new ResponsePackage[String](e.getMessage, HttpStatus.SC_UNAVAILABLE_FOR_LEGAL_REASONS).toResponse(response)


    case e: Exception =>
      new ResponsePackage[String]("Unexpected server error occurred.", HttpStatus.SC_INTERNAL_SERVER_ERROR).toResponse(response)
  }
}