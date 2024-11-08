package com.radovan.spring.services.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpEntity, HttpHeaders, MediaType}
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.radovan.spring.services.EurekaRegistrationService
import java.util

@Service
class EurekaRegistrationServiceImpl extends EurekaRegistrationService {

  @Autowired
  private var restTemplate: RestTemplate = _
  private val EUREKA_SERVER_URL = "http://localhost:8761/eureka/apps"

  @Scheduled(fixedRate = 30000L)
  override def registerService(): Unit = {
    val objectMapper = new ObjectMapper()

    try {
      val appName = "user-service"
      val instanceId = "user-service-01"
      val hostname = "localhost"
      val port = 8081

      // Korišćenje HashMap umesto Scala mutable.Map
      val instanceData = new util.HashMap[String, Any]()
      instanceData.put("instanceId", instanceId)
      instanceData.put("app", appName)
      instanceData.put("hostName", hostname)
      instanceData.put("ipAddr", hostname)
      instanceData.put("statusPageUrl", "http://localhost:8081/info")
      instanceData.put("healthCheckUrl", "http://localhost:8081/health")
      instanceData.put("vipAddress", appName)
      instanceData.put("secureVipAddress", appName)
      instanceData.put("leaseRenewalIntervalInSeconds", 30)
      instanceData.put("leaseExpirationDurationInSeconds", 90)

      // Dodavanje portMap i securePortMap
      val portMap = new util.HashMap[String, Integer]()
      portMap.put("$", port)
      instanceData.put("port", portMap)

      val securePortMap = new util.HashMap[String, Integer]()
      securePortMap.put("$", 0)
      instanceData.put("securePort", securePortMap)

      // Dodavanje dataCenterInfo
      val dataCenterInfo = new util.HashMap[String, String]()
      dataCenterInfo.put("@class", "com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo")
      dataCenterInfo.put("name", "MyOwn") // Koristi "MyOwn" za lokalni centar podataka
      instanceData.put("dataCenterInfo", dataCenterInfo)

      // Kreiranje registrationData kao HashMap
      val registrationData = new util.HashMap[String, Any]()
      registrationData.put("instance", instanceData)

      // Serijalizacija u JSON
      val jsonPayload: JsonNode = objectMapper.valueToTree(registrationData)

      // Priprema HTTP zahteva
      val headers = new HttpHeaders()
      headers.setContentType(MediaType.APPLICATION_JSON)

      val requestEntity = new HttpEntity[JsonNode](jsonPayload, headers)

      val registrationUrl = s"$EUREKA_SERVER_URL/$appName"

      // Slanje podataka na Eureka server
      restTemplate.postForEntity(registrationUrl, requestEntity, classOf[JsonNode])
    } catch {
      case e: Exception =>
        e.printStackTrace()
        throw new RuntimeException("Failed to register service with Eureka", e)
    }
  }
}
