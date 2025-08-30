package com.radovan.scalatra.services
import com.radovan.scalatra.utils.ResponsePackage
import jakarta.servlet.http.HttpServletRequest

trait ApiGatewayService {


  def forwardRequest(serviceName: String, request: HttpServletRequest): ResponsePackage[String]
}
