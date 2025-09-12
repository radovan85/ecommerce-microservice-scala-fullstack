package com.radovan.scalatra.services.impl

import com.radovan.scalatra.services.EurekaServiceDiscovery
import flexjson.JSONDeserializer
import jakarta.inject.Inject
import org.apache.hc.client5.http.classic.methods.HttpGet
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.core5.http.ClassicHttpResponse
import org.apache.hc.core5.http.io.HttpClientResponseHandler
import java.util

import java.nio.file.{Files, Paths}

class EurekaServiceDiscoveryImpl @Inject() () extends EurekaServiceDiscovery {

  private val client = HttpClients.createDefault()
  private val EUREKA_API_SERVICES_URL = "http://eureka-server:8761/eureka/apps"
  private val K8S_TOKEN_PATH = "/var/run/secrets/kubernetes.io/serviceaccount/token"

  override def getServiceUrl(serviceName: String): String = {
    val runningInK8s = sys.env.contains("KUBERNETES_SERVICE_HOST") && Files.exists(Paths.get(K8S_TOKEN_PATH))
    println(s"🔍 Environment check: runningInK8s = $runningInK8s")

    if (runningInK8s) {
      val portEnv = sys.env.getOrElse(s"${serviceName.toUpperCase}_SERVICE_PORT", "80")
      println(s"🌐 Using K8s DNS fallback: serviceName = $serviceName, port = $portEnv")
      return s"http://$serviceName:$portEnv"
    }

    val url = s"$EUREKA_API_SERVICES_URL/$serviceName"
    println(s"🌐 Querying Eureka at: $url")

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

        println(s"📦 Eureka response status: $statusCode")
        println(s"📦 Eureka response body:\n$body")

        if (statusCode >= 200 && statusCode < 300) {
          if (body == null || body.trim.isEmpty) {
            throw new RuntimeException("Eureka registry did not respond properly!")
          }

          val jsonData = new JSONDeserializer[Object]().deserialize(body)
          val map = jsonData.asInstanceOf[java.util.Map[String, Object]]

          val application = Option(map.get("application"))
            .getOrElse {
              println(s"❌ Service $serviceName not found in Eureka registry!")
              throw new RuntimeException(s"Service $serviceName not found in Eureka registry!")
            }
            .asInstanceOf[java.util.Map[String, Object]]

          println(s"🔍 Found application block for $serviceName")

          val instancesObj = application.get("instance")
          val instances: util.List[_] = instancesObj match {
            case list: util.List[_] =>
              println(s"🔍 Multiple instances found (${list.size()})")
              list
            case singleInstance =>
              println(s"🔍 Single instance found")
              util.Collections.singletonList(singleInstance)
          }

          val it = instances.iterator()
          while (it.hasNext) {
            val instance = it.next().asInstanceOf[java.util.Map[String, Object]]

            val address = Option(instance.get("hostName"))
              .map(_.toString)
              .getOrElse {
                println("❌ Missing hostName in instance block")
                throw new RuntimeException("Invalid service data: missing hostName")
              }

            val portMap = instance.get("port").asInstanceOf[java.util.Map[String, Object]]
            val port = Option(portMap.get("$"))
              .map(_.toString.toInt)
              .getOrElse {
                println("❌ Missing port in instance block")
                throw new RuntimeException("Invalid service data: missing port")
              }

            println(s"✅ Discovered service URL: http://$address:$port")
            return s"http://$address:$port"
          }

          println(s"❌ No valid instance found for $serviceName")
          throw new RuntimeException(s"Service not found: $serviceName")
        } else {
          println(s"❌ Eureka returned error status: $statusCode")
          throw new RuntimeException(s"Failed to fetch service URL from Eureka registry. Status: $statusCode, body: $body")
        }
      }
    }

    client.execute(request, responseHandler)
  }
}
