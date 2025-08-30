package com.radovan.play.utils

import play.api.mvc.RequestHeader
import com.radovan.play.exceptions.InstanceUndefinedException

object TokenUtils {

  def provideToken(request: RequestHeader): String = {
    request.headers.get("Authorization")
      .filter(_.startsWith("Bearer "))
      .map(_.stripPrefix("Bearer ").trim)
      .getOrElse(throw new InstanceUndefinedException("Missing or invalid authorization token"))
  }

}

