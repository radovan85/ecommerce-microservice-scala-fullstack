package com.radovan.scalatra.metrics

import com.radovan.scalatra.services.PrometheusService
import jakarta.servlet.http.{HttpServletRequest, HttpServletResponse}

class PrometheusInterceptor(prometheus: PrometheusService) extends MetricsInterceptor {
  override def interceptRequest(request: HttpServletRequest): Unit = {
    prometheus.increaseRequestCount()
    request.setAttribute("startTime", System.nanoTime())
  }

  override def interceptResponse(request: HttpServletRequest, response: HttpServletResponse): Unit = {
    val durationNs = System.nanoTime() - request.getAttribute("startTime").asInstanceOf[Long]
    prometheus.recordResponseTime(durationNs / 1e9)
    prometheus.updateHttpStatusCount(response.getStatus)
  }
}
