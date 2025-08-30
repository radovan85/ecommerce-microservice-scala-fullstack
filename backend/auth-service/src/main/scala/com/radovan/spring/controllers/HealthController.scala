package com.radovan.spring.controllers

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.web.bind.annotation.{GetMapping, RequestMapping, RestController}

@RestController
@RequestMapping(value = Array("/api/health"))
class HealthController {

  @GetMapping
  def healthCheck():ResponseEntity[String] = {
    new ResponseEntity("Ok", HttpStatus.OK)
  }
}
