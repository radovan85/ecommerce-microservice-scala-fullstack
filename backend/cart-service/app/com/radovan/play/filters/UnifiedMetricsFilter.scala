package com.radovan.play.filters

import com.radovan.play.services.PrometheusService
import jakarta.inject.{Inject, Singleton}
import play.api.mvc.{EssentialAction, EssentialFilter}
import scala.concurrent.ExecutionContext

@Singleton
class UnifiedMetricsFilter @Inject()(
                                      prometheus: PrometheusService
                                    )(implicit ec: ExecutionContext) extends EssentialFilter {

  override def apply(next: EssentialAction): EssentialAction = EssentialAction { requestHeader =>
    // 🚫 Preskoči metrikovanje za Prometheus scrape i Health check
    if (requestHeader.path == "/prometheus" || requestHeader.path == "/api/health") {
      next(requestHeader)
    } else {
      val startTimeNs = System.nanoTime()
      prometheus.increaseRequestCount()

      next(requestHeader).map { result =>
        val durationSec = (System.nanoTime() - startTimeNs) / 1e9
        prometheus.recordResponseTime(durationSec)
        prometheus.updateHttpStatusCount(result.header.status)
        result
      }
    }
  }
}