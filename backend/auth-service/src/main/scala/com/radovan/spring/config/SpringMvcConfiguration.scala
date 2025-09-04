package com.radovan.spring.config

import com.fasterxml.jackson.databind.{ObjectMapper, SerializationFeature}
import org.modelmapper.ModelMapper
import org.modelmapper.config.Configuration.AccessLevel
import org.modelmapper.convention.MatchingStrategies
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.{Bean, ComponentScan, Configuration, Primary}
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.client.RestTemplate
import com.radovan.spring.interceptors.{AuthInterceptor, UnifiedMetricsInterceptor}
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.prometheusmetrics.{PrometheusConfig, PrometheusMeterRegistry}
import org.springframework.web.servlet.config.annotation.{EnableWebMvc, InterceptorRegistry, WebMvcConfigurer}

@Configuration
@EnableScheduling
@ComponentScan(basePackages = Array("com.radovan.spring"))
@EnableWebMvc
class SpringMvcConfiguration extends WebMvcConfigurer {


  private var authInterceptor: AuthInterceptor = _
  private var metricsInterceptor:UnifiedMetricsInterceptor = _

  @Autowired
  private def initialize(authInterceptor: AuthInterceptor,metricsInterceptor: UnifiedMetricsInterceptor):Unit = {
    this.authInterceptor = authInterceptor
    this.metricsInterceptor = metricsInterceptor
  }


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
  def getObjectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)

  @Bean
  def getRestTemplate: RestTemplate = new RestTemplate()

  @Bean
  @Primary
  def prometheusMeterRegistry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT)


  @Bean
  def meterRegistry(prometheusRegistry: PrometheusMeterRegistry):MeterRegistry = prometheusRegistry

  override def addInterceptors(registry: InterceptorRegistry): Unit = {
    registry.addInterceptor(authInterceptor)
      .excludePathPatterns("/prometheus")
    registry.addInterceptor(metricsInterceptor)
      .excludePathPatterns("/prometheus")
  }
}
