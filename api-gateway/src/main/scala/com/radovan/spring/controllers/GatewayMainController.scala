package com.radovan.spring.controllers

import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.web.bind.annotation.{GetMapping, RestController}

@RestController
class GatewayMainController {

  @GetMapping(value = Array("/"))
  def entryPoint(): ResponseEntity[String] = {
    new ResponseEntity("Api Gateway endpoint", HttpStatus.OK)
  }
}

