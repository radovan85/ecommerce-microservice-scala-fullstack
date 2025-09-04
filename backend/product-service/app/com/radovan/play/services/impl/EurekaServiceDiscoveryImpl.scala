package com.radovan.play.services.impl

import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.radovan.play.services.EurekaServiceDiscovery
import jakarta.inject.{Inject, Singleton}
import play.libs.ws.WSClient

import scala.jdk.CollectionConverters._

@Singleton
class EurekaServiceDiscoveryImpl @Inject() (
                                             wsClient: WSClient
                                           ) extends EurekaServiceDiscovery {

  private val EUREKA_API_SERVICES_URL = "http://eureka-server:8761/eureka/apps"
  private val objectMapper = new ObjectMapper()

  override def getServiceUrl(serviceName: String): String = {
    val responseJson: JsonNode = wsClient.url(EUREKA_API_SERVICES_URL)
      .addHeader("Accept", "application/json")
      .get()
      .toCompletableFuture
      .join()
      .asJson()

    if (responseJson == null || responseJson.isEmpty)
      throw new RuntimeException("No services found in Eureka registry")


    val appsNodeOpt = Option(responseJson.get("applications")).flatMap(n => Option(n.get("application")))
    val apps = appsNodeOpt.map(_.elements().asScala).getOrElse(Seq.empty)

    for (app <- apps) {
      val appNameOpt = Option(app.get("name")).map(_.asText())
      if (appNameOpt.exists(_.equalsIgnoreCase(serviceName))) {
        val instancesOpt = Option(app.get("instance")).map(_.elements().asScala)
        for {
          instances <- instancesOpt
          instance <- instances
          address <- Option(instance.get("hostName")).map(_.asText())
          port <- Option(instance.get("port")).flatMap(p => Option(p.get("$")).map(_.asInt()))
        } {
          return s"http://$address:$port"
        }
      }
    }

    throw new RuntimeException(s"Service not found: $serviceName")
  }


}
