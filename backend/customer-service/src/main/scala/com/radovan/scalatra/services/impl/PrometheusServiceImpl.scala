package com.radovan.scalatra.services.impl

import com.radovan.scalatra.services.PrometheusService
import io.micrometer.core.instrument.{Counter, Gauge, MeterRegistry, Timer}
import jakarta.inject.{Inject, Singleton}

import java.lang.management.{ManagementFactory, MemoryMXBean, OperatingSystemMXBean, ThreadMXBean}
import java.util.concurrent.TimeUnit

@Singleton
class PrometheusServiceImpl @Inject()(registry: MeterRegistry) extends PrometheusService {

  private val requestCounter: Counter = registry.counter("api_requests_total")
  private val dbQueryCounter: Counter = registry.counter("db_query_total")
  private val externalApiLatencyCounter: Counter = registry.counter("external_api_latency_seconds")
  private val responseTimer: Timer = registry.timer("response_time_seconds")

  private val memoryMXBean: MemoryMXBean = ManagementFactory.getMemoryMXBean
  private val threadMXBean: ThreadMXBean = ManagementFactory.getThreadMXBean
  private val osMXBean: OperatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean

  import java.util.function.ToDoubleFunction

  private val heapUsageGauge: Gauge = Gauge
    .builder("heap_used_bytes", memoryMXBean, new ToDoubleFunction[MemoryMXBean] {
      override def applyAsDouble(bean: MemoryMXBean): Double =
        bean.getHeapMemoryUsage.getUsed.toDouble
    })
    .register(registry)

  private val heapAllocationRateGauge: Gauge = Gauge
    .builder("heap_allocation_rate", memoryMXBean, new ToDoubleFunction[MemoryMXBean] {
      override def applyAsDouble(bean: MemoryMXBean): Double =
        bean.getHeapMemoryUsage.getCommitted.toDouble
    })
    .register(registry)

  private val activeThreadsGauge: Gauge = Gauge
    .builder("active_threads_total", threadMXBean, new ToDoubleFunction[ThreadMXBean] {
      override def applyAsDouble(bean: ThreadMXBean): Double =
        bean.getThreadCount.toDouble
    })
    .register(registry)

  private val cpuLoadGauge: Gauge = Gauge
    .builder("cpu_load_percentage", osMXBean, new ToDoubleFunction[OperatingSystemMXBean] {
      override def applyAsDouble(bean: OperatingSystemMXBean): Double =
        bean.getSystemLoadAverage
    })
    .register(registry)

  private val activeSessionsGauge: Gauge = Gauge
    .builder("active_sessions", threadMXBean, new ToDoubleFunction[ThreadMXBean] {
      override def applyAsDouble(bean: ThreadMXBean): Double =
        bean.getPeakThreadCount.toDouble
    })
    .register(registry)


  override def increaseRequestCount(): Unit = requestCounter.increment()

  override def recordResponseTime(duration: Double): Unit =
    responseTimer.record((duration * 1000).toLong, TimeUnit.MILLISECONDS)

  override def updateMemoryUsage(): Unit = heapUsageGauge.value()

  override def updateThreadCount(): Unit = activeThreadsGauge.value()

  override def updateCpuLoad(): Unit = cpuLoadGauge.value()

  override def updateDatabaseQueryCount(): Unit = dbQueryCounter.increment()

  override def updateHeapAllocationRate(): Unit = heapAllocationRateGauge.value()

  override def updateActiveSessions(): Unit = activeSessionsGauge.value()

  override def updateHttpStatusCount(statusCode: Int): Unit = {
    val statusClass = statusCode / 100
    statusClass match {
      case 2 => registry.counter("http_requests_status_total", "status", "2xx").increment()
      case 4 => registry.counter("http_requests_status_total", "status", "4xx").increment()
      case 5 => registry.counter("http_requests_status_total", "status", "5xx").increment()
      case _ => () // ignoriši ostale (1xx, 3xx, itd.)
    }
  }


  override def updateExternalApiLatency(duration: Double): Unit =
    externalApiLatencyCounter.increment(duration)
}

