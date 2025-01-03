package com.radovan.spring.services

trait EurekaServiceDiscovery {

  def getServiceUrl(serviceName:String):String
}
