package com.radovan.scalatra.utils

import com.radovan.scalatra.exceptions.InstanceUndefinedException
import jakarta.servlet.http.HttpServletRequest

object TokenUtils {

  def provideToken(request: HttpServletRequest): String = {
    Option(request.getHeader("Authorization"))
      .filter(_.startsWith("Bearer "))
      .map(_.stripPrefix("Bearer ").trim)
      .getOrElse(throw new InstanceUndefinedException("Missing or invalid authorization token"))
  }

}

