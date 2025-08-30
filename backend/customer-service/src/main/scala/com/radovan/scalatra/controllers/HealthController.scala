package com.radovan.scalatra.controllers

import com.radovan.scalatra.security.CorsHandler
import com.radovan.scalatra.utils.ResponsePackage
import org.apache.hc.core5.http.HttpStatus
import org.scalatra.ScalatraServlet

class HealthController extends ScalatraServlet with CorsHandler{

  get("/"){
    new ResponsePackage[String]("OK",HttpStatus.SC_OK)
  }
}
