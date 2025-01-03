package com.radovan.spring.services.impl

import java.util.{HashMap, Map => JMap}
import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.radovan.spring.services.EurekaRegistrationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpEntity, HttpHeaders, MediaType}
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

import java.net.InetAddress

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

      val appName = "user-service"
      val instanceId = appName + "-01"
      val port = 8081

      // Kreiranje podataka za registraciju koristeći java.util.Map
      val instanceData: JMap[String, Any] = new HashMap()
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
      val portMap: JMap[String, Any] = new HashMap()
      portMap.put("$", port)
      instanceData.put("port", portMap)

      val securePortMap: JMap[String, Any] = new HashMap()
      securePortMap.put("$", 0)
      instanceData.put("securePort", securePortMap)

      // Dodavanje metadata
      val metadata: JMap[String, Any] = new HashMap()
      metadata.put("management.port", port)
      instanceData.put("metadata", metadata)

      // Dodavanje dataCenterInfo
      val dataCenterInfo: JMap[String, Any] = new HashMap()
      dataCenterInfo.put("@class", "com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo")
      dataCenterInfo.put("name", "MyOwn")
      instanceData.put("dataCenterInfo", dataCenterInfo)

      // Glavni JSON objekat sa "instance" ključem
      val registrationData: JMap[String, Any] = new HashMap()
      registrationData.put("instance", instanceData)

      // Konverzija u JSONNode
      val jsonPayload: JsonNode = objectMapper.valueToTree(registrationData)

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
