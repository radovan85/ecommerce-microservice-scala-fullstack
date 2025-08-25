package com.radovan.scalatra.services.impl

import com.radovan.scalatra.config.ApacheHttpClientSync
import com.radovan.scalatra.services.EurekaRegistrationService
import jakarta.inject.{Inject, Singleton}
import org.apache.pekko.actor.{ActorSystem, Cancellable}
import org.apache.pekko.stream.Materializer

import java.net.InetAddress
import flexjson.JSONSerializer

import scala.collection.mutable
import scala.concurrent.ExecutionContext
import scala.jdk.CollectionConverters._

@Singleton
class EurekaRegistrationServiceImpl @Inject() (
                                                system: ActorSystem,
                                                mat: Materializer
                                              ) extends EurekaRegistrationService {

  implicit val ec: ExecutionContext = system.dispatcher

  private val eurekaServerUrl = "http://localhost:8761/eureka/apps"
  private val appName = "api-gateway"
  private val port = 8080

  private val scheduler: Cancellable = system.scheduler.scheduleAtFixedRate(
    initialDelay = scala.concurrent.duration.Duration.Zero,
    interval = scala.concurrent.duration.Duration(30, "seconds")
  )(() => registerService())

  override def registerService(): Unit = {

    try {
      val hostname = InetAddress.getLocalHost.getHostName
      val ipAddr = InetAddress.getLocalHost.getHostAddress
      val instanceId = "api-gateway-01"

      val instanceData = mutable.LinkedHashMap[String, Any]()

      instanceData.put("instanceId", "api-gateway-01")
      instanceData.put("app", "api-gateway")
      instanceData.put("hostName", hostname)
      instanceData.put("ipAddr", ipAddr)
      instanceData.put("statusPageUrl", s"http://$ipAddr:$port/info")
      instanceData.put("healthCheckUrl", s"http://$ipAddr:$port/health")
      instanceData.put("homePageUrl", s"http://$ipAddr:$port/")
      instanceData.put("vipAddress", "api-gateway")
      instanceData.put("secureVipAddress", "api-gateway")
      instanceData.put("leaseRenewalIntervalInSeconds", 30)
      instanceData.put("leaseExpirationDurationInSeconds", 90)

      val portMap = mutable.LinkedHashMap[String, Any]()
      portMap.put("$", port)
      portMap.put("@enabled", true)
      instanceData.put("port", portMap.asJava)

      val securePortMap = mutable.LinkedHashMap[String, Any]()
      securePortMap.put("$", 0)
      securePortMap.put("@enabled", false)
      instanceData.put("securePort", securePortMap.asJava)

      val metadata = mutable.LinkedHashMap[String, Any]()
      metadata.put("management.port", port)

      val dataCenterInfo = mutable.LinkedHashMap[String, Any]()
      dataCenterInfo.put("@class", "com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo")
      dataCenterInfo.put("name", "MyOwn")

      // Ubacivanje u instanceData
      instanceData.put("metadata", metadata.asJava)
      instanceData.put("dataCenterInfo", dataCenterInfo.asJava)

      val registrationData = mutable.LinkedHashMap[String, Any]()
      registrationData.put("instance", instanceData.asJava)

      val jsonString = new JSONSerializer()
        .exclude("*.class")
        .deepSerialize(registrationData.asJava)

      val headers = Map(
        "Content-Type" -> "application/json",
        "Accept" -> "application/json"
      )

      val registrationUrl = s"$eurekaServerUrl/${appName.toUpperCase}"


      val (statusCode, responseBody) = ApacheHttpClientSync.post(
        registrationUrl,
        jsonString,
        Map.empty
      )



      if (statusCode >= 200 && statusCode < 300) {
        println(s"✅ Successfully registered service at $registrationUrl")
      } else {
        println(s"❌ Failed to register service. Status: $statusCode, Body: $responseBody")
      }

    } catch {
      case ex: Exception =>
        println(s"💥 Exception in registerService: ${ex.getMessage}")
        ex.printStackTrace()
    }
  }



}
