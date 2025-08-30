package com.radovan.play.services.impl

import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.radovan.play.services.EurekaRegistrationService
import jakarta.inject.{Inject, Singleton}
import org.apache.pekko.actor.ActorSystem
import play.libs.ws.{WSClient, WSResponse}

import java.net.InetAddress
import scala.collection.mutable
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.jdk.CollectionConverters._

@Singleton
class EurekaRegistrationServiceImpl @Inject() (
                                                wsClient: WSClient,
                                                objectMapper: ObjectMapper,
                                                actorSystem: ActorSystem,
                                                executionContext: ExecutionContext
                                              ) extends EurekaRegistrationService {

  private val EUREKA_SERVER_URL = "http://localhost:8761/eureka/apps"
  private val appName = "order-service"
  private val instanceId = s"$appName-01"
  private val port = 9003

  // 🕒 Periodično zakazivanje registracije
  actorSystem.scheduler.scheduleAtFixedRate(
    initialDelay = 0.seconds,
    interval = 30.seconds
  )(() => registerService())(executionContext)

  override def registerService(): Unit = {
    try {
      val hostname = InetAddress.getLocalHost.getHostName
      val ipAddr = InetAddress.getLocalHost.getHostAddress

      println(s"Hostname: $hostname")
      println(s"IP Address: $ipAddr")

      val instanceData = new mutable.LinkedHashMap[String, Any]()
      instanceData.put("instanceId", instanceId)
      instanceData.put("app", appName)
      instanceData.put("hostName", hostname)
      instanceData.put("ipAddr", ipAddr)
      instanceData.put("statusPageUrl", s"http://$ipAddr:$port/info")
      instanceData.put("healthCheckUrl", s"http://$ipAddr:$port/health")
      instanceData.put("homePageUrl", s"http://$ipAddr:$port/")
      instanceData.put("vipAddress", appName)
      instanceData.put("secureVipAddress", appName)
      instanceData.put("leaseRenewalIntervalInSeconds", Int.box(30))
      instanceData.put("leaseExpirationDurationInSeconds", Int.box(90))

      val portMap = new mutable.LinkedHashMap[String, Any]()
      portMap.put("$", Int.box(port))
      instanceData.put("port", portMap.asJava)

      val securePortMap = new mutable.LinkedHashMap[String, Any]()
      securePortMap.put("$", Int.box(0))
      instanceData.put("securePort", securePortMap.asJava)

      val metadata = new mutable.LinkedHashMap[String, Any]()
      metadata.put("management.port", Int.box(port))
      instanceData.put("metadata", metadata.asJava)

      val dataCenterInfo = new mutable.LinkedHashMap[String, Any]()
      dataCenterInfo.put("@class", "com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo")
      dataCenterInfo.put("name", "MyOwn")
      instanceData.put("dataCenterInfo", dataCenterInfo.asJava)

      val registrationData = new mutable.LinkedHashMap[String, Any]()
      registrationData.put("instance", instanceData.asJava)

      val jsonPayload: JsonNode = objectMapper.valueToTree(registrationData.asJava)
      val registrationUrl = s"$EUREKA_SERVER_URL/$appName"

      wsClient.url(registrationUrl)
        .addHeader("Content-Type", "application/json")
        .post(jsonPayload)
        .thenAccept(handleResponse)

    } catch {
      case e: Exception =>
        e.printStackTrace()
        throw new RuntimeException("Failed to register service with Eureka", e)
    }
  }

  private def handleResponse(response: WSResponse): Unit = {
    println(s"Eureka server response status: ${response.getStatus}")
    if (response.getStatus == 204 || response.getStatus == 200) {
      println("Service registered successfully!")
    } else {
      System.err.println(s"Failed to register service with Eureka: ${response.getStatusText}")
      System.err.println(s"Response body: ${response.getBody}")
    }
  }
}

