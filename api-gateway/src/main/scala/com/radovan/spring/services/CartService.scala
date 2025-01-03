package com.radovan.spring.services

import com.fasterxml.jackson.databind.JsonNode

trait CartService {

  def getMyCart:JsonNode

  def clearCart:String
}
