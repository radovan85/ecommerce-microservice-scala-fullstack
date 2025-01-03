package com.radovan.spring.services

import com.fasterxml.jackson.databind.JsonNode

trait OrderAddressService {

  def listAll:Array[JsonNode]
}
