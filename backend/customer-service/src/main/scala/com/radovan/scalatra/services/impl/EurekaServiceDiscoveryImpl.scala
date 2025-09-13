package com.radovan.scalatra.services.impl

import com.radovan.scalatra.services.EurekaServiceDiscovery
import flexjson.JSONDeserializer
import jakarta.inject.Inject
import org.apache.hc.client5.http.classic.methods.HttpGet
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.core5.http.ClassicHttpResponse
import org.apache.hc.core5.http.io.HttpClientResponseHandler

import java.util

class EurekaServiceDiscoveryImpl @Inject() () extends EurekaServiceDiscovery {

  private val client = HttpClients.createDefault()

  private val EUREKA_API_SERVICES_URL = "http://localhost:8761/eureka/apps"

  override def getServiceUrl(serviceName: String): String = {
    val url = s"$EUREKA_API_SERVICES_URL/$serviceName"

    val request = new HttpGet(url)
    request.addHeader("Accept", "application/json")

    val responseHandler = new HttpClientResponseHandler[String] {
      override def handleResponse(response: ClassicHttpResponse): String = {
        val statusCode = response.getCode
        val entity = response.getEntity
        val body = if (entity != null) {
          try {
            org.apache.hc.core5.http.io.entity.EntityUtils.toString(entity, "UTF-8")
          } catch {
            case e: Exception =>
              println(s"❌ Failed to read response body: ${e.getMessage}")
              ""
          }
        } else ""


        if (statusCode >= 200 && statusCode < 300) {
          if (body == null || body.trim.isEmpty) {
            throw new RuntimeException("Eureka registry did not respond properly!")
          }

          val jsonData = new JSONDeserializer[Object]().deserialize(body)
          val map = jsonData.asInstanceOf[java.util.Map[String, Object]]

          val application = Option(map.get("application"))
            .getOrElse(throw new RuntimeException(s"Service $serviceName not found in Eureka registry!"))
            .asInstanceOf[java.util.Map[String, Object]]

          val instancesObj = application.get("instance")
          val instances: util.List[_] = instancesObj match {
            case list: util.List[_] => list
            case singleInstance => util.Collections.singletonList(singleInstance)
          }

          val it = instances.iterator()
          while (it.hasNext) {
            val instance = it.next().asInstanceOf[java.util.Map[String, Object]]

            val address = Option(instance.get("hostName"))
              .map(_.toString)
              .getOrElse(throw new RuntimeException("Invalid service data: missing hostName"))

            val portMap = instance.get("port").asInstanceOf[java.util.Map[String, Object]]
            val port = Option(portMap.get("$"))
              .map(_.toString.toInt)
              .getOrElse(throw new RuntimeException("Invalid service data: missing port"))

            if (address.nonEmpty && port != 0) {
              return s"http://$address:$port"
            }
          }

          throw new RuntimeException(s"Service not found: $serviceName")
        } else {
          throw new RuntimeException(s"Failed to fetch service URL from Eureka registry. Status: $statusCode, body: $body")
        }
      }
    }

    client.execute(request, responseHandler)
  }
}
