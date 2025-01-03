package com.radovan.spring.security

import jakarta.servlet.ServletException
import jakarta.servlet.http.{HttpServletRequest, HttpServletResponse}
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

import java.io.IOException

@Component
@SerialVersionUID(1L)
class JwtAuthenticationEntryPoint extends AuthenticationEntryPoint with Serializable {

  @throws(classOf[IOException])
  @throws(classOf[ServletException])
  override def commence(request: HttpServletRequest, response: HttpServletResponse, authException: AuthenticationException): Unit = {
    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")
  }
}

