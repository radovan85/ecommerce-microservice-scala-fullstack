package com.radovan.play.utils

import flexjson.JSONSerializer
import play.api.mvc.{Result, Results}
import play.api.http.ContentTypes

class ResponsePackage[T](val body: T, val statusCode: Int) {

  private def toJson: String = {
    new JSONSerializer()
      .exclude("*.class")
      .prettyPrint(true)
      .deepSerialize(body)
  }

  def toResult: Result = {
    Results.Status(statusCode)(toJson).as(ContentTypes.JSON)
  }

  override def toString: String = toJson
}