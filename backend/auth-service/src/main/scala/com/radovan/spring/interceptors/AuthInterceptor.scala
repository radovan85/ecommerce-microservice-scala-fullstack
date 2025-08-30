package com.radovan.spring.interceptors

import org.springframework.security.core.{Authentication, context}
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import com.radovan.spring.entity.UserEntity
import com.radovan.spring.exceptions.SuspendedUserException
import jakarta.servlet.http.{HttpServletRequest, HttpServletResponse}

@Component
class AuthInterceptor extends HandlerInterceptor {

  override def preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean = {

    val authentication: Authentication = context.SecurityContextHolder.getContext.getAuthentication

    if (authentication != null && authentication.isAuthenticated) {
      val principal = authentication.getPrincipal

      principal match {
        case userDetails: UserEntity =>
          Option(userDetails.getEnabled).filter(_ == 0).foreach { _ =>
            throw new SuspendedUserException(new Error("User account is suspended"))
          }


          val authHeader = s"Bearer ${authentication.getCredentials}"
          response.setHeader("Authorization", authHeader)

        case _ =>
      }
    }

    true
  }
}

