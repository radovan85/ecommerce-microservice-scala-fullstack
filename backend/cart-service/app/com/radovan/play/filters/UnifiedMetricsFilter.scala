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
    if (requestHeader.path == "/prometheus") {
      next(requestHeader) // 🚫 Preskoči metrikovanje za Prometheus scrape
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
