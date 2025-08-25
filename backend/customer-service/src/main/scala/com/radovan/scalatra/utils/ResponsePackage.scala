package com.radovan.scalatra.utils

import flexjson.JSONSerializer
import jakarta.servlet.http.HttpServletResponse

class ResponsePackage[T](val body: T, val statusCode: Int) { // NEMA defaultne vrijednosti!
  private val headers: Map[String, String] = Map("Content-Type" -> "application/json")

  private def toJson: String = {
    new JSONSerializer()
      .exclude("*.class")
      .prettyPrint(true)
      .deepSerialize(body)
  }

  def toResponse(response: HttpServletResponse): String = {
    response.setStatus(statusCode) // UVIJEK koristi ono što je prosleđeno
    headers.foreach { case (key, value) =>
      response.setHeader(key, value)
    }
    toJson
  }

  override def toString: String = toJson
}
