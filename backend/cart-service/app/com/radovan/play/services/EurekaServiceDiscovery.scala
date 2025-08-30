package com.radovan.play.services

trait EurekaServiceDiscovery {

  def getServiceUrl(serviceName:String):String
}
