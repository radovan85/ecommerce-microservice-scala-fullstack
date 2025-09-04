package com.radovan.scalatra.controllers

import com.radovan.scalatra.config.ApacheHttpClientSync
import com.radovan.scalatra.utils.ServiceUrlProvider
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
import jakarta.inject.Inject
import org.apache.hc.core5.http.HttpStatus
import org.scalatra.ScalatraServlet

class PrometheusController @Inject()(
                                      prometheusRegistry: PrometheusMeterRegistry,
                                      urlProvider: ServiceUrlProvider
                                    ) extends ScalatraServlet {

  get("/") {
    contentType = "text/plain"
    response.setStatus(HttpStatus.SC_OK)
    prometheusRegistry.scrape()
  }

  get("/metrics") {
    contentType = "text/plain"
    val endpointSuffix = "/prometheus"
    val services = Seq(
      urlProvider.getGatewayServiceUrl,
      urlProvider.getAuthServiceUrl,
      urlProvider.getCustomerServiceUrl,
      urlProvider.getCartServiceUrl,
      urlProvider.getOrderServiceUrl,
      urlProvider.getProductServiceUrl
    )


    val allMetrics = services.map { url =>
      val endpoint = s"${url}${endpointSuffix}"

      val headers = Map("Accept" -> "text/plain")
      val (status, body) = ApacheHttpClientSync.get(endpoint, headers)

      if (status >= 200 && status < 300) body
      else s"# Failed to fetch metrics from $url"
    }.mkString("\n")


    response.setStatus(HttpStatus.SC_OK)
    allMetrics
  }
}
