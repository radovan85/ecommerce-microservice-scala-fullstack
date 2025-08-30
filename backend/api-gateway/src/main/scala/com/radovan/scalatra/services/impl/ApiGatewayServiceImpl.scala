package com.radovan.scalatra.services.impl

import com.radovan.scalatra.services.ApiGatewayService
import com.radovan.scalatra.utils.ResponsePackage
import com.radovan.scalatra.utils.ServiceUrlProvider
import jakarta.inject.Inject
import jakarta.servlet.http.HttpServletRequest
import org.apache.hc.client5.http.classic.methods._
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.core5.http.io.entity.{ByteArrayEntity, EntityUtils, StringEntity}
import org.apache.hc.core5.http.ContentType
import org.apache.hc.core5.http.io.HttpClientResponseHandler

import scala.jdk.CollectionConverters._
import java.io.IOException

class ApiGatewayServiceImpl @Inject() () extends ApiGatewayService {

  private val cachedServiceUrls = scala.collection.concurrent.TrieMap.empty[String, String]

  private var urlProvider: ServiceUrlProvider = _

  @Inject
  def initialize(urlProvider: ServiceUrlProvider): Unit = {
    this.urlProvider = urlProvider
  }

  override def forwardRequest(serviceName: String, request: HttpServletRequest): ResponsePackage[String] = {
    val serviceUrl = cachedServiceUrls.getOrElseUpdate(serviceName, urlProvider.getServiceUrl(serviceName))
    if (serviceUrl == null || serviceUrl.isEmpty) {
      return new ResponsePackage(s"Service $serviceName not found", 502)
    }

    val client = HttpClients.createDefault()

    // Metod HTTP metode
    val method = request.getMethod.toUpperCase

    // Konstrukcija pune URL adrese
    val fullUrl = s"$serviceUrl${request.getRequestURI}"

    // Priprema HttpRequest bazirano na metodi
    val httpRequest = method match {
      case "GET" => new HttpGet(fullUrl)
      case "POST" =>
        val post = new HttpPost(fullUrl)
        val bytes = try {
          request.getInputStream.readAllBytes()
        } catch {
          case e: IOException => return new ResponsePackage(s"Error reading request body: ${e.getMessage}", 500)
        }
        post.setEntity(new ByteArrayEntity(bytes, ContentType.APPLICATION_JSON))
        post
      case "PUT" =>
        val put = new HttpPut(fullUrl)
        val bytes = try {
          request.getInputStream.readAllBytes()
        } catch {
          case e: IOException => return new ResponsePackage(s"Error reading request body: ${e.getMessage}", 500)
        }
        put.setEntity(new ByteArrayEntity(bytes, ContentType.APPLICATION_JSON))
        put
      case "PATCH" =>
        val patch = new HttpPatch(fullUrl)
        val bytes = try {
          request.getInputStream.readAllBytes()
        } catch {
          case e: IOException => return new ResponsePackage(s"Error reading request body: ${e.getMessage}", 500)
        }
        patch.setEntity(new ByteArrayEntity(bytes, ContentType.APPLICATION_JSON))
        patch
      case "DELETE" => new HttpDelete(fullUrl)
      case "OPTIONS" => new HttpOptions(fullUrl)
      case other => return new ResponsePackage(s"Unsupported HTTP method: $other", 405)
    }

    // Kopiranje svih headera osim Content-Length
    request.getHeaderNames.asScala.foreach { headerName =>
      if (!headerName.equalsIgnoreCase("content-length")) {
        request.getHeaders(headerName).asScala.foreach { headerValue =>
          httpRequest.addHeader(headerName, headerValue)
        }
      }
    }

    try {
      val handler = new HttpClientResponseHandler[ResponsePackage[String]] {
        override def handleResponse(response: org.apache.hc.core5.http.ClassicHttpResponse): ResponsePackage[String] = {
          val statusCode = response.getCode
          val entity = response.getEntity
          val body = if (entity != null) EntityUtils.toString(entity) else ""
          new ResponsePackage(body, statusCode)
        }
      }

      client.execute(httpRequest, handler)

    } catch {
      case e: Exception =>
        new ResponsePackage(s"HTTP request failed: ${e.getMessage}", 500)
    }
  }
}
