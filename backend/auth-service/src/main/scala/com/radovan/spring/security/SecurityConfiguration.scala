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

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
class SecurityConfiguration {

  @Autowired
  private var jwtRequestFilter: JwtRequestFilter = _

  @Autowired
  private var jwtAuthenticationEntryPoint: JwtAuthenticationEntryPoint = _

  @Autowired
  private var corsHandler: CorsHandler = _

  @Bean
  @throws[Exception]
  def securityFilterChain(http: HttpSecurity): SecurityFilterChain = {
    http
      .csrf(csrf => csrf.disable()) // Onemogući CSRF zaštitu za OAuth2
      .cors(cors => cors.configurationSource(corsHandler))
      .sessionManagement(session => session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .exceptionHandling(exception => exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
      .authorizeHttpRequests(authorize =>
        authorize
          .requestMatchers("/api/auth/login").anonymous()
          .requestMatchers("/api/auth/public-key", "/api/health", "/prometheus").permitAll()
          .anyRequest().authenticated()
      )
      .addFilterBefore(jwtRequestFilter, classOf[UsernamePasswordAuthenticationFilter])
      .build()
  }

  @Bean
  def authenticationManager(
                             userDetailsService: UserDetailsService,
                             passwordEncoder: BCryptPasswordEncoder
                           ): AuthenticationManager = {
    val authProvider = new DaoAuthenticationProvider(userDetailsService)
    authProvider.setPasswordEncoder(passwordEncoder)
    new ProviderManager(authProvider)
  }

  @Bean
  def passwordEncoder(): BCryptPasswordEncoder = new BCryptPasswordEncoder()
}
