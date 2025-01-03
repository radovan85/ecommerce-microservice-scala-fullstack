package com.radovan.spring.services

import com.fasterxml.jackson.databind.JsonNode

trait ShippingAddressService {

  def updateAddress(address:JsonNode):String
}
