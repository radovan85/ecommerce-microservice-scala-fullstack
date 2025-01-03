package com.radovan.spring.config

import org.modelmapper.ModelMapper
import org.modelmapper.config.Configuration.AccessLevel
import org.modelmapper.convention.MatchingStrategies
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.{Bean, ComponentScan, Configuration}
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.client.RestTemplate
import org.springframework.web.servlet.config.annotation.{EnableWebMvc, InterceptorRegistry, WebMvcConfigurer}
import com.radovan.spring.interceptors.AuthInterceptor

@Configuration
@EnableScheduling
@EnableWebMvc
@ComponentScan(basePackages = Array("com.radovan.spring"))
class SpringMvcConfiguration extends WebMvcConfigurer {

  @Autowired
  private var authInterceptor: AuthInterceptor = _

  @Bean
  def getMapper: ModelMapper = {
    val modelMapper = new ModelMapper()
    modelMapper.getConfiguration
      .setAmbiguityIgnored(true)
      .setFieldAccessLevel(AccessLevel.PRIVATE)
      .setMatchingStrategy(MatchingStrategies.STRICT)
    modelMapper
  }

  @Bean
  def getRestTemplate: RestTemplate = new RestTemplate()

  override def addInterceptors(registry: InterceptorRegistry): Unit = {
    registry.addInterceptor(authInterceptor)
  }
}
