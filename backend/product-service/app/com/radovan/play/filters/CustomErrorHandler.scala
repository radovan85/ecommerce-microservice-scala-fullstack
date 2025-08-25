package com.radovan.play.filters

import com.radovan.play.exceptions._
import jakarta.inject.Singleton
import org.apache.hc.core5.http.HttpStatus
import play.api.http.HttpErrorHandler
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent._

@Singleton
class CustomErrorHandler extends HttpErrorHandler {

  override def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
    val root = unwrap(exception)

    val (status, message) = root match {
      case _: DataNotValidatedException        => (HttpStatus.SC_NOT_ACCEPTABLE, root.getMessage)
      case _: InstanceUndefinedException  => (HttpStatus.SC_PRECONDITION_FAILED, root.getMessage)
      case _: ForbiddenAccessException    => (HttpStatus.SC_UNAVAILABLE_FOR_LEGAL_REASONS,root.getMessage)
      case _: FileUploadException         => (HttpStatus.SC_PRECONDITION_FAILED, root.getMessage)
      case _: ExistingInstanceException => (HttpStatus.SC_CONFLICT, root.getMessage)
      case _                              => (HttpStatus.SC_INTERNAL_SERVER_ERROR, s"Server error: ${root.getMessage}")
    }

    Future.successful(Results.Status(status)(Json.toJson(message)))
  }

  override def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = {
    Future.successful(Results.Status(statusCode)(Json.toJson(message)))
  }

  private def unwrap(ex: Throwable): Throwable = {
    ex match {
      case e: java.util.concurrent.CompletionException if e.getCause != null => unwrap(e.getCause)
      case other => other
    }
  }
}
