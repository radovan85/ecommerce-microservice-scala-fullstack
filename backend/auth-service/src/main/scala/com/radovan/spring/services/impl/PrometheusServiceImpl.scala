package com.radovan.spring.services.impl

import com.radovan.spring.services.PrometheusService
import io.micrometer.core.instrument.{Counter, Gauge, MeterRegistry, Timer}
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import java.lang.management.{ManagementFactory, MemoryMXBean, OperatingSystemMXBean, ThreadMXBean}
import java.util.concurrent.TimeUnit

@Service
class PrometheusServiceImpl extends PrometheusService {

  @Autowired
  private var registry: MeterRegistry = _

  // Metrika: brojači i tajmeri
  private var requestCounter: Counter = _
  private var dbQueryCounter: Counter = _
  private var externalApiLatencyCounter: Counter = _
  private var responseTimer: Timer = _

  // Metrika: gauge pokazatelji
  private var heapUsageGauge: Gauge = _
  private var heapAllocationRateGauge: Gauge = _
  private var activeThreadsGauge: Gauge = _
  private var cpuLoadGauge: Gauge = _
  private var activeSessionsGauge: Gauge = _

  // Sistem info
  private val memoryMXBean: MemoryMXBean = ManagementFactory.getMemoryMXBean
  private val threadMXBean: ThreadMXBean = ManagementFactory.getThreadMXBean
  private val osMXBean: OperatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean

  @PostConstruct
  def init(): Unit = {
    // 📈 Brojači
    requestCounter = registry.counter("api_requests_total")
    dbQueryCounter = registry.counter("db_query_total")
    externalApiLatencyCounter = registry.counter("external_api_latency_seconds")
    responseTimer = registry.timer("response_time_seconds")

    // 📊 Gauge metrika
    heapUsageGauge = Gauge
      .builder("heap_used_bytes", memoryMXBean, (bean: MemoryMXBean) => bean.getHeapMemoryUsage.getUsed.toDouble)
      .register(registry)

    heapAllocationRateGauge = Gauge
      .builder("heap_allocation_rate", memoryMXBean, (bean: MemoryMXBean) => bean.getHeapMemoryUsage.getCommitted.toDouble)
      .register(registry)

    activeThreadsGauge = Gauge
      .builder("active_threads_total", threadMXBean, (bean: ThreadMXBean) => bean.getThreadCount.toDouble)
      .register(registry)

    cpuLoadGauge = Gauge
      .builder("cpu_load_percentage", osMXBean, (bean: OperatingSystemMXBean) => bean.getSystemLoadAverage)
      .register(registry)

    activeSessionsGauge = Gauge
      .builder("active_sessions", threadMXBean, (bean: ThreadMXBean) => bean.getPeakThreadCount.toDouble)
      .register(registry)
  }

  // 🔁 Override metodi za metrikaciju

  override def increaseRequestCount(): Unit =
    requestCounter.increment()

  override def recordResponseTime(duration: Double): Unit =
    responseTimer.record((duration * 1000).toLong, TimeUnit.MILLISECONDS)

  override def updateMemoryUsage(): Unit =
    heapUsageGauge.value()

  override def updateThreadCount(): Unit =
    activeThreadsGauge.value()

  override def updateCpuLoad(): Unit =
    cpuLoadGauge.value()

  override def updateDatabaseQueryCount(): Unit =
    dbQueryCounter.increment()

  override def updateHeapAllocationRate(): Unit =
    heapAllocationRateGauge.value()

  override def updateActiveSessions(): Unit =
    activeSessionsGauge.value()

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

  override def updateControllerMethodTags(controller: String, method: String): Unit =
    registry.counter("http_requests_by_controller_method", "controller", controller, "method", method).increment()
}
