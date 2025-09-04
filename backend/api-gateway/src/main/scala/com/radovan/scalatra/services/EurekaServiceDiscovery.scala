package com.radovan.scalatra.services

trait EurekaServiceDiscovery {

  def getServiceUrl(serviceName: String): String
}
