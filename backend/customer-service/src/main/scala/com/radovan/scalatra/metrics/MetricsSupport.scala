package com.radovan.scalatra.metrics

import com.radovan.scalatra.services.PrometheusService
import org.scalatra.ScalatraBase

trait MetricsSupport extends ScalatraBase {
  def prometheusService: PrometheusService

  before() {
    val interceptor = new PrometheusInterceptor(prometheusService)
    interceptor.interceptRequest(request)
  }

  after() {
    val interceptor = new PrometheusInterceptor(prometheusService)
    interceptor.interceptResponse(request, response)
  }
}

