package com.radovan.spring

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@SpringBootApplication
@EnableDiscoveryClient
class AuthServiceApp

object AuthServiceApp {
  def main(args: Array[String]): Unit = {
    SpringApplication.run(classOf[AuthServiceApp], args: _*)
  }
}

