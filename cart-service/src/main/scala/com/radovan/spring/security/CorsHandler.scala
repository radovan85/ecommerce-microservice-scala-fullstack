package com.radovan.spring.security

import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Component
import org.springframework.web.cors.{CorsConfiguration, CorsConfigurationSource}

import java.util


@Component class CorsHandler extends CorsConfigurationSource {

  override def getCorsConfiguration(request: HttpServletRequest): CorsConfiguration = {
    val returnValue = new CorsConfiguration
    returnValue.setAllowedOriginPatterns(util.Arrays.asList("*"))
    returnValue.setAllowCredentials(true)
    returnValue.setAllowedHeaders(util.Arrays.asList("Access-Control-Allow-Headers", "Access-Control-Allow-Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers", "Origin", "Cache-Control", "Content-Type", "Authorization"))
    returnValue.setAllowedMethods(util.Arrays.asList("DELETE", "GET", "POST", "PATCH", "PUT"))
    returnValue
  }
}
