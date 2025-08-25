package com.radovan.play.utils

import com.fasterxml.jackson.databind.JsonNode
import com.radovan.play.exceptions.InstanceUndefinedException
import jakarta.inject.Inject
import play.api.libs.ws.WSClient
import play.api.mvc.RequestHeader
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class NodeUtils {

  private var urlProvider: ServiceUrlProvider = _
  private var wsClient: WSClient = _

  @Inject
  private def initialize(urlProvider: ServiceUrlProvider, wsClient: WSClient): Unit = {
    this.urlProvider = urlProvider
    this.wsClient = wsClient
  }

  def provideAuthCustomerNode(request: RequestHeader): JsonNode = {
    val token = TokenUtils.provideToken(request)
    val url = s"${urlProvider.getCustomerServiceUrl}/api/customers/me"

    val futureResponse = wsClient.url(url)
      .addHttpHeaders("Authorization" -> s"Bearer $token")
      .get()
      .map(_.json.as[JsonNode])
      .recover {
        case ex =>
          throw new InstanceUndefinedException(s"Failed to retrieve customer node: ${ex.getMessage}")
      }

    Await.result(futureResponse, 5.seconds)
  }
}

