package com.radovan.scalatra.controllers

import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
import jakarta.inject.Inject
import org.apache.hc.core5.http.HttpStatus
import org.scalatra.ScalatraServlet

class PrometheusController @Inject()(
                                      prometheusRegistry: PrometheusMeterRegistry
                                    ) extends ScalatraServlet {

  get("/") {
    contentType = "text/plain"
    response.setStatus(HttpStatus.SC_OK)
    prometheusRegistry.scrape()
  }

}
