package com.radovan.scalatra.utils

import com.radovan.scalatra.services.EurekaServiceDiscovery
import jakarta.inject.Inject

import scala.collection.concurrent.TrieMap

class ServiceUrlProvider @Inject() (eurekaServiceDiscovery: EurekaServiceDiscovery) {

  private val cachedServiceUrls = TrieMap.empty[String, String]

  def getServiceUrl(serviceName: String): String = {
    cachedServiceUrls.getOrElseUpdate(serviceName, {
      val url = eurekaServiceDiscovery.getServiceUrl(serviceName)
      validateUrl(url, serviceName)
      url
    })
  }

  def getAuthServiceUrl: String = getServiceUrl("auth-service")
  def getCustomerServiceUrl: String = getServiceUrl("customer-service")
  def getOrderServiceUrl: String = getServiceUrl("order-service")
  def getCartServiceUrl: String = getServiceUrl("cart-service")
  def getProductServiceUrl: String = getServiceUrl("product-service")

  private def validateUrl(url: String, serviceName: String): Unit = {
    if (url == null || !url.startsWith("http")) {
      throw new IllegalArgumentException(s"Invalid URL for $serviceName: $url")
    }
  }
}
