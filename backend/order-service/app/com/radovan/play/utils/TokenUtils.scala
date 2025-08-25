package com.radovan.play.utils

import com.radovan.play.exceptions.InstanceUndefinedException
import play.api.mvc.RequestHeader

object TokenUtils {

  def provideToken(request: RequestHeader): String = {
    request.headers.get("Authorization")
      .filter(_.startsWith("Bearer "))
      .map(_.stripPrefix("Bearer ").trim)
      .getOrElse(throw new InstanceUndefinedException("Missing or invalid authorization token"))
  }

}

