package com.radovan.spring.interceptors

import org.springframework.security.core.{Authentication}
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import com.radovan.spring.exceptions.SuspendedUserException
import com.radovan.spring.utils.CustomUserDetails
import jakarta.servlet.http.{HttpServletRequest, HttpServletResponse}
import org.springframework.security.core.context.SecurityContextHolder


@Component
class AuthInterceptor extends HandlerInterceptor {

  @throws[Exception]
  override def preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean = {

    // Pribavi autentifikaciju iz SecurityContext-a
    val authentication: Authentication = SecurityContextHolder.getContext.getAuthentication

    if (authentication != null && authentication.isAuthenticated) {
      val principal = authentication.getPrincipal

      // Provera da li je principal instance CustomUserDetails
      principal match {
        case userDetails: CustomUserDetails =>
          // Provera vrednosti "enabled" atributa
          val enabled: Byte = userDetails.getEnabled
          if (enabled == 0) {
            throw new SuspendedUserException(new Error("User account is suspended"))
          }

          // Dodaj "Authorization" zaglavlje u odgovor sa trenutnim tokenom
          val authHeader = s"Bearer ${authentication.getCredentials}" // ili koristi pravu metodu za token
          response.setHeader("Authorization", authHeader)

        case _ => // Ako nije instanca CustomUserDetails, ignoriši
      }
    }

    true
  }
}
