package com.radovan.spring.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.security.authentication.{AuthenticationManager, ProviderManager}
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

import com.radovan.spring.services.impl.UserDetailsImpl

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
class SecurityConfiguration {

  private var jwtRequestFilter: JwtRequestFilter = _
  private var jwtAuthenticationEntryPoint: JwtAuthenticationEntryPoint = _
  private var corsHandler: CorsHandler = _
  private var userDetails: UserDetailsImpl = _

  @Autowired
  private def initialize(jwtRequestFilter: JwtRequestFilter,jwtAuthenticationEntryPoint: JwtAuthenticationEntryPoint,
                         corsHandler: CorsHandler,userDetails:UserDetailsImpl):Unit = {
    this.jwtRequestFilter = jwtRequestFilter
    this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint
    this.corsHandler = corsHandler
    this.userDetails = userDetails
  }

  @Bean
  @throws[Exception]
  def securityFilterChain(http: HttpSecurity): SecurityFilterChain = {
    http
      .csrf(csrf => csrf.disable()) // Onemogući CSRF zaštitu
      .cors(cors => cors.configurationSource(corsHandler))
      .sessionManagement(session => session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .exceptionHandling(exception => exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
      .authorizeHttpRequests(authorize =>
        authorize
          .requestMatchers("/api/auth/login", "api/auth/register", "/api/health").anonymous()
          .requestMatchers("/api/auth/public-key").permitAll()
          .anyRequest().authenticated()
      )
      .addFilterBefore(jwtRequestFilter, classOf[UsernamePasswordAuthenticationFilter])
      .build()
  }

  @Bean
  def authenticationManager(): AuthenticationManager = {
    val authProvider = new DaoAuthenticationProvider()
    authProvider.setUserDetailsService(userDetails)
    authProvider.setPasswordEncoder(passwordEncoder())
    new ProviderManager(authProvider)
  }

/*
  @Bean
  def userDetailsService(): UserDetailsService = {
    new UserDetailsImpl()
  }

 */



  @Bean
  def passwordEncoder(): BCryptPasswordEncoder = {
    new BCryptPasswordEncoder()
  }
}

