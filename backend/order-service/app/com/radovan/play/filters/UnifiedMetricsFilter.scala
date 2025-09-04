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
    val startTimeNs = System.nanoTime()

    // 📈 Broj zahteva odmah
    prometheus.increaseRequestCount()

    next(requestHeader).map { result =>
      val durationSec = (System.nanoTime() - startTimeNs) / 1e9

      // ⏱ Trajanje odgovora
      prometheus.recordResponseTime(durationSec)

      // 📊 Status klasifikacija (2xx, 4xx, 5xx)
      prometheus.updateHttpStatusCount(result.header.status)

      result
    }
  }
}
