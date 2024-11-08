package com.radovan.spring.security

import java.io.IOException

import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

@Component
class JwtAuthenticationEntryPoint extends AuthenticationEntryPoint with Serializable {

  @throws(classOf[IOException])
  @throws(classOf[ServletException])
  override def commence(request: HttpServletRequest, response: HttpServletResponse, authException: AuthenticationException): Unit = {
    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")
  }
}

