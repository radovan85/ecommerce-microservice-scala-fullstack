package com.radovan.spring.security

import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.security.oauth2.client.registration.{ClientRegistration, ClientRegistrationRepository, InMemoryClientRegistrationRepository}
import org.springframework.security.oauth2.core.{AuthorizationGrantType, ClientAuthenticationMethod}

@Configuration
class OAuth2ClientConfig {

  @Bean
  def clientRegistrationRepository(): ClientRegistrationRepository = {
    val clientRegistration = ClientRegistration.withRegistrationId("customer-service")
      .clientId("customer-service")
      .clientSecret("{noop}customer-secret")
      .scope("read")
      .authorizationUri("http://user-service/oauth2/authorize")
      .tokenUri("http://user-service/oauth2/token")
      .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
      .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
      .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
      .build()

    new InMemoryClientRegistrationRepository(clientRegistration)
  }
}

