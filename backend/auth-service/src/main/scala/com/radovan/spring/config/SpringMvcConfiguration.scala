package com.radovan.spring.config


import com.radovan.spring.interceptors.{AuthInterceptor, UnifiedMetricsInterceptor}
import com.radovan.spring.utils.NatsUtils
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.prometheusmetrics.{PrometheusConfig, PrometheusMeterRegistry}
import org.modelmapper.ModelMapper
import org.modelmapper.convention.MatchingStrategies
import org.springframework.beans.factory.ObjectProvider
import org.springframework.context.annotation.{Bean, ComponentScan, Configuration, Primary}
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.client.RestTemplate
import org.springframework.web.servlet.config.annotation.{EnableWebMvc, InterceptorRegistry, WebMvcConfigurer}
import tools.jackson.databind.ObjectMapper

@Configuration
@EnableWebMvc
@ComponentScan(Array("com.radovan.spring"))
@EnableScheduling
class SpringMvcConfiguration(
                              private val authInterceptorProvider: ObjectProvider[AuthInterceptor],
                              private val metricsInterceptorProvider: ObjectProvider[UnifiedMetricsInterceptor]
                            ) extends WebMvcConfigurer {

  @Bean
  def getObjectMapper: ObjectMapper = new ObjectMapper()

  @Bean
  def getMapper: ModelMapper = {
    val returnValue = new ModelMapper()
    returnValue.getConfiguration
      .setAmbiguityIgnored(true)
      .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
      .setMatchingStrategy(MatchingStrategies.STRICT)
    returnValue
  }

  @Bean
  def getRestTemplate: RestTemplate = new RestTemplate()

  @Bean
  def getNatsUtils: NatsUtils = new NatsUtils()

  @Bean
  @Primary
  def prometheusMeterRegistry: PrometheusMeterRegistry =
    new PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

  @Bean
  def meterRegistry(prometheusRegistry: PrometheusMeterRegistry): MeterRegistry =
    prometheusRegistry

  override def addInterceptors(registry: InterceptorRegistry): Unit = {
    val authInterceptor = authInterceptorProvider.getIfAvailable
    val metricsInterceptor = metricsInterceptorProvider.getIfAvailable

    if (authInterceptor != null) {
      registry.addInterceptor(authInterceptor).excludePathPatterns("/prometheus")
    }
    if (metricsInterceptor != null) {
      registry.addInterceptor(metricsInterceptor).excludePathPatterns("/prometheus", "/api/health")
    }
  }


}
