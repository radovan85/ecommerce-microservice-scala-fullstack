package com.radovan.spring.config

import com.radovan.spring.interceptors.{AuthInterceptor, RestTemplateHeaderModifierInterceptor}
import org.modelmapper.ModelMapper
import org.modelmapper.config.Configuration.AccessLevel
import org.modelmapper.convention.MatchingStrategies
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.{Bean, ComponentScan, Configuration}
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.client.RestTemplate
import org.springframework.web.servlet.config.annotation.{EnableWebMvc, InterceptorRegistry, WebMvcConfigurer}

import java.util.Collections

@Configuration
@EnableScheduling
@EnableWebMvc
@ComponentScan(basePackages = Array("com.radovan.spring"))
class SpringMvcConfiguration extends WebMvcConfigurer {

  @Autowired
  private var authInterceptor: AuthInterceptor = _

  @Bean
  def getMapper: ModelMapper = {
    val returnValue = new ModelMapper
    returnValue.getConfiguration.setAmbiguityIgnored(true).setFieldAccessLevel(AccessLevel.PRIVATE)
    returnValue.getConfiguration.setMatchingStrategy(MatchingStrategies.STRICT)
    returnValue
  }

  @Bean
  def restTemplate: RestTemplate = {
    val restTemplate = new RestTemplate()
    restTemplate.setInterceptors(Collections.singletonList(new RestTemplateHeaderModifierInterceptor()))
    restTemplate
  }

  override def addInterceptors(registry: InterceptorRegistry): Unit = {
    registry.addInterceptor(authInterceptor)
  }
}

