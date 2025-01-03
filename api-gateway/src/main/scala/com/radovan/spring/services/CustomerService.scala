package com.radovan.spring.services

import com.fasterxml.jackson.databind.JsonNode

trait CustomerService {

  def addCustomer(registrationForm:JsonNode):JsonNode

  def listAll:Array[JsonNode]

  def deleteCustomer(customerId:Integer):String

  def suspendCustomer(customerId:Integer):String

  def reactivateCustomer(customerId:Integer):String


}
