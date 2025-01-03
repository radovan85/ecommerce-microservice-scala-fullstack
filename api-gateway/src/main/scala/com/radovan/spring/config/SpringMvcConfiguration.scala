package com.radovan.spring.config

import com.radovan.spring.interceptors.RestTemplateHeaderModifierInterceptor
import org.springframework.context.annotation.{Bean, ComponentScan, Configuration}
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.client.RestTemplate
import org.springframework.web.servlet.config.annotation.{EnableWebMvc, WebMvcConfigurer}

import java.util.Collections

@Configuration
@EnableScheduling
@EnableWebMvc
@ComponentScan(basePackages = Array("com.radovan.spring"))
class SpringMvcConfiguration extends WebMvcConfigurer {


  @Bean
  def restTemplate: RestTemplate = {
    val restTemplate = new RestTemplate()
    restTemplate.setInterceptors(Collections.singletonList(new RestTemplateHeaderModifierInterceptor()))
    restTemplate
  }


}

