package com.radovan.scalatra.metrics

import jakarta.servlet.http.{HttpServletRequest, HttpServletResponse}

trait MetricsInterceptor {
  def interceptRequest(request: HttpServletRequest): Unit
  def interceptResponse(request: HttpServletRequest, response: HttpServletResponse): Unit
}

