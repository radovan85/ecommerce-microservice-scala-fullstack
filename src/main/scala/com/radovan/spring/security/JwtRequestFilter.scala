package com.radovan.spring.security

import java.io.IOException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import com.radovan.spring.converter.TempConverter
import com.radovan.spring.dto.UserDto
import com.radovan.spring.services.UserService
import com.radovan.spring.utils.JwtUtil
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

@Component
class JwtRequestFilter extends OncePerRequestFilter {

  private var userService: UserService = _

  private var jwtUtil: JwtUtil = _

  private var tempConverter: TempConverter = _

  @Autowired
  private def injectAll(userService: UserService, jwtUtil: JwtUtil, tempConverter: TempConverter): Unit = {
    this.userService = userService
    this.tempConverter = tempConverter
    this.jwtUtil = jwtUtil
  }

  @throws(classOf[IOException])
  @throws(classOf[ServletException])
  override protected def doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain): Unit = {
    val authorizationHeader: String = request.getHeader("Authorization")
    var email: String = null
    var jwt: String = null

    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
      jwt = authorizationHeader.substring(7)
      try {
        email = jwtUtil.extractUsername(jwt)
      } catch {
        case ex: Exception =>
          val error: Error = new Error("Destroyed token")
          System.out.println(error)
      }
    }

    if (email != null && SecurityContextHolder.getContext.getAuthentication == null) {
      val userDto: UserDto = userService.getUserByEmail(email)
      val userDetails: UserDetails = tempConverter.userDtoToEntity(userDto)
      if (jwtUtil.validateToken(jwt, userDetails)) {
        val usernamePasswordAuthenticationToken: UsernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
          userDetails, null, userDetails.getAuthorities)
        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request))
        SecurityContextHolder.getContext.setAuthentication(usernamePasswordAuthenticationToken)
      }
    }

    filterChain.doFilter(request, response)
  }
}

