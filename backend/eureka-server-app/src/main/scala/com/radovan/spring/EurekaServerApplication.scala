package com.radovan.spring

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer

@SpringBootApplication
@EnableEurekaServer
class EurekaServerApplication

object EurekaServerApplication {
  def main(args: Array[String]): Unit = {
    SpringApplication.run(classOf[EurekaServerApplication], args: _*)
  }
}
