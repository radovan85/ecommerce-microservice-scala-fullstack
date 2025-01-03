package com.radovan.spring.utils

import com.radovan.spring.services.EurekaServiceDiscovery
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import scala.collection.mutable

@Component
class ServiceUrlProvider @Autowired()(eurekaServiceDiscovery: EurekaServiceDiscovery) {

  private val cachedServiceUrls: mutable.Map[String, String] = mutable.Map()

  def getServiceUrl(serviceName: String): String = {
    cachedServiceUrls.getOrElseUpdate(serviceName, {
      try {
        val serviceUrl = eurekaServiceDiscovery.getServiceUrl(serviceName)
        validateUrl(serviceUrl, serviceName)
        serviceUrl
      } catch {
        case e: RuntimeException =>
          println(s"Failed to retrieve service URL for: $serviceName - ${e.getMessage}")
          throw e
      }
    })
  }

  private def validateUrl(url: String, serviceName: String): Unit = {
    if (url == null || !url.startsWith("http")) {
      throw new IllegalArgumentException(s"Invalid URL for $serviceName: $url")
    }
  }

  def getCartServiceUrl: String = getServiceUrl("CART-SERVICE")

  def getUserServiceUrl: String = getServiceUrl("USER-SERVICE")
}
