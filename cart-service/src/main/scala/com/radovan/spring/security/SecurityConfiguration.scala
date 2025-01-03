package com.radovan.spring.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
class SecurityConfiguration {

  private var jwtRequestFilter: JwtRequestFilter = _
  private var jwtAuthenticationEntryPoint: JwtAuthenticationEntryPoint = _
  private var corsHandler: CorsHandler = _

  @Autowired
  private def initialize(jwtRequestFilter: JwtRequestFilter, jwtAuthenticationEntryPoint: JwtAuthenticationEntryPoint,
                         corsHandler: CorsHandler): Unit = {
    this.jwtRequestFilter = jwtRequestFilter
    this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint
    this.corsHandler = corsHandler
  }

  @Bean
  def securityFilterChain(http: HttpSecurity): SecurityFilterChain = {
    http
      .csrf(csrf => csrf.disable())
      .cors(cors => cors.configurationSource(corsHandler))
      .sessionManagement(session => session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .exceptionHandling(exception => exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
      .authorizeHttpRequests(auth => auth
        .requestMatchers("/cart/addCart").anonymous()
        .anyRequest().authenticated()
      )
      .addFilterBefore(jwtRequestFilter, classOf[UsernamePasswordAuthenticationFilter])
      .build()
  }
}
