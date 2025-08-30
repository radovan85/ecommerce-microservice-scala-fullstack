package com.radovan.scalatra.controllers

import com.radovan.scalatra.config.CorsHandler
import com.radovan.scalatra.services.ApiGatewayService
import com.radovan.scalatra.utils.ResponsePackage
import jakarta.servlet.http.HttpServletRequest
import org.apache.pekko.http.scaladsl.model.StatusCodes.{BadRequest, NoContent, NotFound}
import org.scalatra.ScalatraServlet


class ApiGatewayController(apiGatewayService: ApiGatewayService)
  extends ScalatraServlet with CorsHandler {

  get("/*") {
    handleRequest(request)
  }

  post("/*") {
    handleRequest(request)
  }

  put("/*") {
    handleRequest(request)
  }

  delete("/*") {
    handleRequest(request)
  }

  options("/*") {
    NoContent
  }



  private def handleRequest(request: HttpServletRequest): Any = {
    val path = request.getRequestURI.stripPrefix("/api/")
    val firstSegmentOpt = path.split("/").headOption

    firstSegmentOpt match {
      case Some(firstSegment) =>
        mapSegmentToService(firstSegment) match {
          case Some(serviceName) =>
            try {
              val responsePackage: ResponsePackage[String] = apiGatewayService.forwardRequest(serviceName, request)
              status = responsePackage.statusCode
              contentType = "application/json"
              responsePackage.body
            } catch {
              case e: Exception =>
                status = 502
                contentType = "text/plain"
                s"Error forwarding request to $serviceName: ${e.getMessage}"
            }
          case None =>
            status = 404
            contentType = "text/plain"
            s"Unknown service for segment: $firstSegment"
        }
      case None =>
        status = 400
        contentType = "text/plain"
        "Invalid API request format"
    }
  }


  private def mapSegmentToService(segment: String): Option[String] = {
    val serviceMappings = Map(
      "products"   -> "product-service",
      "categories" -> "product-service",
      "auth"       -> "auth-service",
      "cart"       -> "cart-service",
      "order"      -> "order-service",
      "customers"  -> "customer-service",
      "addresses"  -> "customer-service"
    )
    serviceMappings.get(segment)
  }
}
