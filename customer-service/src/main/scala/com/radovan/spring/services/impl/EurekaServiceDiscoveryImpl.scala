package com.radovan.spring.services.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpMethod, ResponseEntity}
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import com.radovan.spring.services.EurekaServiceDiscovery
import flexjson.JSONDeserializer
import java.util
import scala.jdk.CollectionConverters._

@Service
class EurekaServiceDiscoveryImpl @Autowired()(restTemplate: RestTemplate) extends EurekaServiceDiscovery {

  private val EUREKA_SERVER_URL = "http://localhost:8761/eureka/apps"

  @SuppressWarnings(Array("unchecked"))
  override def getServiceUrl(serviceName: String): String = {
    val url = s"$EUREKA_SERVER_URL/$serviceName"
    val response: ResponseEntity[String] = restTemplate.exchange(url, HttpMethod.GET, null, classOf[String])

    val responseBody = response.getBody
    if (responseBody == null) {
      throw new RuntimeException(s"Service not found: $serviceName")
    }

    // Deserialize JSON response directly into util.Map
    val app: util.Map[String, Any] = new JSONDeserializer[util.Map[String, Any]]().deserialize(responseBody)
    if (app == null || !app.containsKey("application")) {
      throw new RuntimeException(s"Service not found: $serviceName")
    }

    val application = app.get("application").asInstanceOf[util.Map[String, Any]]
    val instanceObj = application.get("instance")

    // Handle list of instances or a single instance
    val instances: List[Map[String, Any]] = instanceObj match {
      case list: util.List[_] =>
        list.asScala.toList.map(_.asInstanceOf[util.Map[String, Any]].asScala.toMap)
      case singleInstance: util.Map[_, _] =>
        List(singleInstance.asInstanceOf[util.Map[String, Any]].asScala.toMap)
      case _ =>
        throw new RuntimeException(s"Unexpected instance type: ${instanceObj.getClass}")
    }

    // Find the first valid homePageUrl
    instances
      .flatMap(_.get("homePageUrl").collect { case url: String => url })
      .headOption
      .getOrElse(throw new RuntimeException(s"homePageUrl not found for service: $serviceName"))
  }
}
