package com.radovan.play.utils

import play.api.http.ContentTypes
import play.api.mvc.{Result, Results}
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule

class ResponsePackage[T](val body: T, val statusCode: Int) {

  private val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)

  private def toJson: String = {
    body match {
      case bytes: Array[Byte] =>
        val base64 = java.util.Base64.getEncoder.encodeToString(bytes)
        mapper.writeValueAsString(Map(
          "contentType" -> "application/octet-stream",
          "base64" -> base64
        ))
      case _ =>
        mapper.writeValueAsString(body)
    }
  }

  def toResult: Result = {
    Results.Status(statusCode)(toJson).as(ContentTypes.JSON)
  }

  override def toString: String = toJson
}
