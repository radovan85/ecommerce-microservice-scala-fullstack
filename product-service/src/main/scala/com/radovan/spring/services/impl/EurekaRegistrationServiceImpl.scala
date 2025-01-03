package com.radovan.spring.services.impl

import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.radovan.spring.services.EurekaRegistrationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpEntity, HttpHeaders, MediaType}
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

import java.net.InetAddress
import scala.collection.mutable
import scala.jdk.CollectionConverters._

@Service
class EurekaRegistrationServiceImpl  extends EurekaRegistrationService {

  private val EUREKA_SERVER_URL = "http://localhost:8761/eureka/apps"
  @Autowired
  private var restTemplate:RestTemplate = _

  @Scheduled(fixedRate = 30000L)
  override def registerService(): Unit = {
    val objectMapper = new ObjectMapper()

    try {
      // Dinamičko dohvaćanje IP adrese i hostname-a
      val hostname = InetAddress.getLocalHost.getHostName
      val ipAddr = InetAddress.getLocalHost.getHostAddress

      val appName = "product-service"
      val instanceId = appName + "-01"
      val port = 8084

      // Kreiranje podataka za registraciju koristeći java.util.Map
      val instanceData = new mutable.HashMap[String,Any]()
      instanceData.put("instanceId", instanceId)
      instanceData.put("app", appName)
      instanceData.put("hostName", hostname) // Koristi hostname
      instanceData.put("ipAddr", ipAddr)     // Dinamički dohvaćena IP adresa
      instanceData.put("statusPageUrl", s"http://$ipAddr:$port/info")
      instanceData.put("healthCheckUrl", s"http://$ipAddr:$port/health")
      instanceData.put("homePageUrl", s"http://$ipAddr:$port/")
      instanceData.put("vipAddress", appName)
      instanceData.put("secureVipAddress", appName)
      instanceData.put("leaseRenewalIntervalInSeconds", 30)
      instanceData.put("leaseExpirationDurationInSeconds", 90)

      // Dodavanje porta
      val portMap = new mutable.HashMap[String, Any]()
      portMap.put("$", port)
      instanceData.put("port", portMap.asJava)

      val securePortMap = new mutable.HashMap[String, Any]()
      securePortMap.put("$", 0)
      instanceData.put("securePort", securePortMap.asJava)

      // Dodavanje metadata
      val metadata = new mutable.HashMap[String,Any]()
      metadata.put("management.port", port)
      instanceData.put("metadata", metadata.asJava)

      // Dodavanje dataCenterInfo
      val dataCenterInfo = new mutable.HashMap[String, Any]()
      dataCenterInfo.put("@class", "com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo")
      dataCenterInfo.put("name", "MyOwn")
      instanceData.put("dataCenterInfo", dataCenterInfo.asJava)

      // Glavni JSON objekat sa "instance" ključem
      val registrationData = new mutable.HashMap[String, Any]()
      registrationData.put("instance", instanceData.asJava)

      // Konverzija u JSONNode
      val jsonPayload = objectMapper.valueToTree[JsonNode](registrationData.asJava)

      // Postavljanje Content-Type zaglavlja
      val headers = new HttpHeaders()
      headers.setContentType(MediaType.APPLICATION_JSON)

      // Kreiranje HttpEntity sa JSON telom i zaglavljem
      val requestEntity = new HttpEntity[JsonNode](jsonPayload, headers)

      // Slanje POST zahteva
      val registrationUrl = s"$EUREKA_SERVER_URL/$appName"
      restTemplate.postForEntity(registrationUrl, requestEntity, classOf[JsonNode])

    } catch {
      case e: Exception =>
        e.printStackTrace()
        throw new RuntimeException("Failed to register service with Eureka", e)
    }
  }
}
