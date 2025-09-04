package com.radovan.spring.controllers

import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpStatus, MediaType, ResponseEntity}
import org.springframework.web.bind.annotation.{GetMapping, RequestMapping, RestController}

@RestController
@RequestMapping(value=Array("/prometheus"))
class PrometheusController {

  private var registry:PrometheusMeterRegistry = _

  @Autowired
  private def initialize(registry: PrometheusMeterRegistry):Unit = {
    this.registry = registry
  }

  @GetMapping(produces = Array(MediaType.TEXT_PLAIN_VALUE))
  def getMetrics: ResponseEntity[String] = new ResponseEntity(registry.scrape(), HttpStatus.OK)

}
