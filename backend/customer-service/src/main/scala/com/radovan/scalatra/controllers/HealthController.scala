package com.radovan.scalatra.controllers

import com.radovan.scalatra.security.CorsHandler
import com.radovan.scalatra.services.PrometheusService
import com.radovan.scalatra.utils.ResponsePackage
import jakarta.inject.Inject
import org.apache.hc.core5.http.HttpStatus
import org.scalatra.ScalatraServlet

class HealthController @Inject()(
                                  val prometheusService: PrometheusService
                                ) extends ScalatraServlet with CorsHandler  {

  get("/") {
    new ResponsePackage[String]("OK", HttpStatus.SC_OK)
  }
}
