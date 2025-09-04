package com.radovan.spring.config

import com.radovan.spring.services.EurekaRegistrationService
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class EurekaClientRegistrationInitializer @Autowired()(private val eurekaRegistrationService: EurekaRegistrationService) {

  @PostConstruct
  def initialize(): Unit = {
    try {
      eurekaRegistrationService.registerService()
    } catch {
      case e: Exception =>
        println(s"Error during service registration: ${e.getMessage}")
        e.printStackTrace()
    }
  }
}
