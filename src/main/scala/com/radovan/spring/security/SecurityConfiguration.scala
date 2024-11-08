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
import org.springframework.security.oauth2.core.{AuthorizationGrantType, ClientAuthenticationMethod}
import org.springframework.security.oauth2.server.authorization.client.{InMemoryRegisteredClientRepository, RegisteredClient, RegisteredClientRepository}
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


  @Bean
  def securityFilterChain(http: HttpSecurity): SecurityFilterChain = {
    http.csrf(csrf => csrf.disable()) // Onemogući CSRF zaštitu za OAuth2
      .cors(cors => cors.configurationSource(corsHandler))
      .sessionManagement(session => session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .exceptionHandling(exception => exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
      .authorizeHttpRequests(authorize => authorize
        .requestMatchers("/login", "/addUser").anonymous()
        .requestMatchers("/oauth2/**").permitAll()
        .requestMatchers("/roles/**", "/currentUser", "/userData/**").permitAll()
        .anyRequest.authenticated())
      .addFilterBefore(jwtRequestFilter, classOf[UsernamePasswordAuthenticationFilter])
      .build()
  }

  @Bean
  def authenticationManager(): AuthenticationManager = {
    val authProvider = new DaoAuthenticationProvider()
    authProvider.setUserDetailsService(userDetailsService())
    authProvider.setPasswordEncoder(passwordEncoder())
    new ProviderManager(authProvider)
  }

  @Bean
  def userDetailsService(): UserDetailsService = new UserDetailsImpl()

  @Bean
  def passwordEncoder(): BCryptPasswordEncoder = new BCryptPasswordEncoder()

  @Bean
  def registeredClientRepository(): RegisteredClientRepository = {
    val customerServiceClient = RegisteredClient.withId("customer-service-client")
      .clientId("customer-service")
      .clientSecret("{noop}customer-secret")
      .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
      .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
      .scope("read")
      .build()

    val cartServiceClient = RegisteredClient.withId("cart-service-client")
      .clientId("cart-service")
      .clientSecret("{noop}cart-secret")
      .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
      .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
      .scope("read")
      .build()

    val productServiceClient = RegisteredClient.withId("product-service-client")
      .clientId("product-service")
      .clientSecret("{noop}product-secret")
      .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
      .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
      .scope("read")
      .build()

    val orderServiceClient = RegisteredClient.withId("order-service-client")
      .clientId("order-service")
      .clientSecret("{noop}order-secret")
      .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
      .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
      .scope("read")
      .build()

    new InMemoryRegisteredClientRepository(
      customerServiceClient, cartServiceClient, productServiceClient, orderServiceClient
    )
  }

  @Autowired
  private def initialize(jwtRequestFilter: JwtRequestFilter, jwtAuthenticationEntryPoint: JwtAuthenticationEntryPoint,
                         corsHandler: CorsHandler): Unit = {
    this.jwtRequestFilter = jwtRequestFilter
    this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint
    this.corsHandler = corsHandler
  }
}

