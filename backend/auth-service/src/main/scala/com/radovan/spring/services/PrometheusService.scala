package com.radovan.spring.services

trait PrometheusService {

  def increaseRequestCount(): Unit

  def recordResponseTime(duration: Double): Unit

  def updateMemoryUsage(): Unit

  def updateThreadCount(): Unit

  def updateCpuLoad(): Unit

  def updateDatabaseQueryCount(): Unit

  def updateHeapAllocationRate(): Unit

  def updateActiveSessions(): Unit

  def updateHttpStatusCount(statusCode: Int): Unit

  def updateExternalApiLatency(duration: Double): Unit

  def updateControllerMethodTags(controller: String, method: String): Unit
}
