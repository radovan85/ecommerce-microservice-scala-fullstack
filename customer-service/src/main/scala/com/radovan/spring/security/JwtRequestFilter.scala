package com.radovan.spring.security

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.{HttpServletRequest, HttpServletResponse}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import com.radovan.spring.utils.JwtUtil

import java.io.IOException

@Component
class JwtRequestFilter extends OncePerRequestFilter {

  private var jwtUtil: JwtUtil = _
  private var userDetailsService: UserDetailsService = _

  @Autowired
  private def initialize(jwtUtil: JwtUtil, userDetailsService: UserDetailsService): Unit = {
    this.jwtUtil = jwtUtil
    this.userDetailsService = userDetailsService
  }

  @throws[ServletException]
  @throws[IOException]
  override def doFilterInternal(
                                 request: HttpServletRequest,
                                 response: HttpServletResponse,
                                 chain: FilterChain
                               ): Unit = {
    try {
      val authorizationHeader = Option(request.getHeader("Authorization"))

      val jwt = authorizationHeader
        .filter(_.startsWith("Bearer "))
        .map(_.substring(7))

      jwt.foreach { token =>
        val username = jwtUtil.extractUsername(token)

        if (username != null && SecurityContextHolder.getContext.getAuthentication == null) {
          if (jwtUtil.validateToken(token, username)) {
            val userDetails = userDetailsService.loadUserByUsername(username)
            val authToken = new UsernamePasswordAuthenticationToken(
              userDetails,
              null,
              userDetails.getAuthorities
            )
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request))
            SecurityContextHolder.getContext.setAuthentication(authToken)
          }
        }
      }

    } catch {
      case ex: Exception =>
        println("Error during JWT filtering", ex)
    }

    chain.doFilter(request, response)
  }
}
