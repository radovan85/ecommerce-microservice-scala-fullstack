package com.radovan.scalatra.controllers

<<<<<<< HEAD
import com.radovan.scalatra.config.ApacheHttpClientSync
import com.radovan.scalatra.utils.ServiceUrlProvider
=======
import com.radovan.scalatra.services.PrometheusService
import com.radovan.scalatra.utils.{ResponsePackage, ServiceUrlProvider}
import com.radovan.scalatra.config.ApacheHttpClientSync
>>>>>>> 5b997bc28e9eae90ccd8ac621149bafaa1f732f4
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
import jakarta.inject.Inject
import org.apache.hc.core5.http.HttpStatus
import org.scalatra.ScalatraServlet

<<<<<<< HEAD
class PrometheusController @Inject()(
                                      prometheusRegistry: PrometheusMeterRegistry,
                                      urlProvider: ServiceUrlProvider
                                    ) extends ScalatraServlet {

  get("/") {
=======
class PrometheusController @Inject() (
                                       prometheusService: PrometheusService,
                                       prometheusRegistry: PrometheusMeterRegistry,
                                       urlProvider: ServiceUrlProvider
                                     ) extends ScalatraServlet {

  get("/prometheus") {
>>>>>>> 5b997bc28e9eae90ccd8ac621149bafaa1f732f4
    contentType = "text/plain"
    response.setStatus(HttpStatus.SC_OK)
    prometheusRegistry.scrape()
  }

<<<<<<< HEAD
  get("/metrics") {
    contentType = "text/plain"
    val endpointSuffix = "/prometheus"
    val services = Seq(
      urlProvider.getGatewayServiceUrl,
=======
  get("/test") {
    prometheusService.increaseRequestCount()
    new ResponsePackage[String]("Counter increased!", HttpStatus.SC_OK).toResponse(response)
  }

  get("/memory") {
    prometheusService.updateMemoryUsage()
    new ResponsePackage[String]("Heap memory updated!", HttpStatus.SC_OK).toResponse(response)
  }

  get("/threads") {
    prometheusService.updateThreadCount()
    new ResponsePackage[String]("Thread count updated!", HttpStatus.SC_OK).toResponse(response)
  }

  get("/cpu") {
    prometheusService.updateCpuLoad()
    new ResponsePackage[String]("CPU load updated!", HttpStatus.SC_OK).toResponse(response)
  }

  get("/response-time") {
    prometheusService.recordResponseTime(0.1)
    new ResponsePackage[String]("Response time metric recorded!", HttpStatus.SC_OK).toResponse(response)
  }

  get("/db-queries") {
    prometheusService.updateDatabaseQueryCount()
    new ResponsePackage[String]("Database query metric updated!", HttpStatus.SC_OK).toResponse(response)
  }

  get("/heap-allocation") {
    prometheusService.updateHeapAllocationRate()
    new ResponsePackage[String]("Heap allocation rate updated!", HttpStatus.SC_OK).toResponse(response)
  }

  get("/active-sessions") {
    prometheusService.updateActiveSessions()
    new ResponsePackage[String]("Active sessions metric updated!", HttpStatus.SC_OK).toResponse(response)
  }

  get("/http-status") {
    prometheusService.updateHttpStatusCount(200)
    new ResponsePackage[String]("HTTP status metric recorded!", HttpStatus.SC_OK).toResponse(response)
  }

  get("/external-api") {
    prometheusService.updateExternalApiLatency(0.5)
    new ResponsePackage[String]("External API latency metric recorded!", HttpStatus.SC_OK).toResponse(response)
  }

  get("/metrics/all") {
    contentType = "text/plain"
    val services = Seq(
>>>>>>> 5b997bc28e9eae90ccd8ac621149bafaa1f732f4
      urlProvider.getAuthServiceUrl,
      urlProvider.getCustomerServiceUrl,
      urlProvider.getCartServiceUrl,
      urlProvider.getOrderServiceUrl,
      urlProvider.getProductServiceUrl
    )

<<<<<<< HEAD

    val allMetrics = services.map { url =>
      val endpoint = s"${url}${endpointSuffix}"

      val headers = Map("Accept" -> "text/plain")
      val (status, body) = ApacheHttpClientSync.get(endpoint, headers)

=======
    val allMetrics = services.map { url =>
      val (status, body) = ApacheHttpClientSync.get(url)
>>>>>>> 5b997bc28e9eae90ccd8ac621149bafaa1f732f4
      if (status >= 200 && status < 300) body
      else s"# Failed to fetch metrics from $url"
    }.mkString("\n")

<<<<<<< HEAD

=======
>>>>>>> 5b997bc28e9eae90ccd8ac621149bafaa1f732f4
    response.setStatus(HttpStatus.SC_OK)
    allMetrics
  }
}
