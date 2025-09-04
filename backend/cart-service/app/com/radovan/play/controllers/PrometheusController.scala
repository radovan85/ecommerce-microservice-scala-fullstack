package com.radovan.play.controllers

import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
import jakarta.inject.Inject
import play.api.mvc.{AbstractController, ControllerComponents, Action, AnyContent}

class PrometheusController @Inject()(
                                      cc: ControllerComponents,
                                      prometheusRegistry: PrometheusMeterRegistry
                                    ) extends AbstractController(cc) {

  def getMetrics: Action[AnyContent] = Action {
    val scrapeData = prometheusRegistry.scrape()
    Ok(scrapeData).as("text/plain")
  }
}
