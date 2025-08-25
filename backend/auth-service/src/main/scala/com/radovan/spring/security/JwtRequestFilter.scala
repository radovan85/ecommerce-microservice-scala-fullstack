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
import jakarta.servlet.http.{HttpServletRequest, HttpServletResponse}

@Component
class JwtRequestFilter extends OncePerRequestFilter {

  private var userService: UserService = _
  private var jwtUtil: JwtUtil = _
  private var tempConverter: TempConverter = _

  @Autowired
  private def initialize(jwtUtil: JwtUtil, tempConverter: TempConverter, userService: UserService): Unit = {
    this.jwtUtil = jwtUtil
    this.tempConverter = tempConverter
    this.userService = userService
  }

  @throws[ServletException]
  @throws[IOException]
  override protected def doFilterInternal(
                                           request: HttpServletRequest,
                                           response: HttpServletResponse,
                                           filterChain: FilterChain
                                         ): Unit = {

    val tokenOpt = extractToken(request)
    if (tokenOpt.isEmpty || !jwtUtil.validateToken(tokenOpt.get)) {
      filterChain.doFilter(request, response)
      return
    }

    val token = tokenOpt.get
    val email = jwtUtil.extractUsername(token)

    if (email != null && SecurityContextHolder.getContext.getAuthentication == null) {
      setAuthentication(email, token, request)
    }

    filterChain.doFilter(request, response)
  }

  private def extractToken(request: HttpServletRequest): Option[String] = {
    Option(request.getHeader("Authorization"))
      .filter(_.startsWith("Bearer "))
      .map(_.substring(7))
  }

  private def setAuthentication(email: String, token: String, request: HttpServletRequest): Unit = {
    val userDto: UserDto = userService.getUserByEmail(email)
    val userDetails: UserDetails = tempConverter.userDtoToEntity(userDto)

    val authentication =
      new UsernamePasswordAuthenticationToken(userDetails, token, userDetails.getAuthorities)

    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request))
    SecurityContextHolder.getContext.setAuthentication(authentication)
  }
}
