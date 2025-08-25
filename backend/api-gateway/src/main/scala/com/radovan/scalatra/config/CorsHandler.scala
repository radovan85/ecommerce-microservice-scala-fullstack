package com.radovan.scalatra.config

import org.scalatra._

trait CorsHandler extends ScalatraBase {

  before() {
    response.setHeader("Access-Control-Allow-Origin", "*")
    response.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS")
    response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With")
  }

  options("/*") {
    response.setHeader("Access-Control-Allow-Origin", "*")
    response.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS")
    response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With")
  }
}

