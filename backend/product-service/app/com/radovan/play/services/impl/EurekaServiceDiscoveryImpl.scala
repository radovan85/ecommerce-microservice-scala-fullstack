package com.radovan.play.services.impl

import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.radovan.play.services.EurekaServiceDiscovery
import jakarta.inject.{Inject, Singleton}
import play.libs.ws.WSClient

import java.nio.file.{Files, Paths}
import scala.jdk.CollectionConverters._
import scala.util.Try

@Singleton
class EurekaServiceDiscoveryImpl @Inject() (
                                             wsClient: WSClient
                                           ) extends EurekaServiceDiscovery {

  private val EUREKA_API_SERVICES_URL = "http://eureka-server:8761/eureka/apps"
  private val K8S_TOKEN_PATH = "/var/run/secrets/kubernetes.io/serviceaccount/token"
  private val objectMapper = new ObjectMapper()

  override def getServiceUrl(serviceName: String): String = {
    val runningInK8s = sys.env.contains("KUBERNETES_SERVICE_HOST") && Files.exists(Paths.get(K8S_TOKEN_PATH))

    if (runningInK8s) {
      // Idiomatski K8s pristup: koristi DNS ime servisa i port iz env varijable
      val portEnv = sys.env.getOrElse(s"${serviceName.toUpperCase}_SERVICE_PORT", "80")
      val port = Try(portEnv.toInt).getOrElse {
        throw new RuntimeException(s"Invalid or missing port env for service '$serviceName'")
      }
      return s"http://$serviceName:$port"
    }

    // Fallback na Eureka (Docker Compose)
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
