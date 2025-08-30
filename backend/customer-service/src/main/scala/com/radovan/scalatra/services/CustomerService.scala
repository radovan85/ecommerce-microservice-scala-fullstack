package com.radovan.scalatra.services

import com.radovan.scalatra.dto.CustomerDto
import com.radovan.scalatra.utils.RegistrationForm
import jakarta.servlet.http.HttpServletRequest

trait CustomerService {

  def addCustomer(form: RegistrationForm): CustomerDto

  def getCustomerById(customerId: Integer): CustomerDto

  def getCustomerByUserId(userId: Integer): CustomerDto

  def listAll: Array[CustomerDto]

  def getCurrentCustomer(jwtToken:String): CustomerDto

  def suspendCustomer(customerId: Integer, jwtToken:String): Unit

  def reactivateCustomer(customerId: Integer, jwtToken:String): Unit

  def removeCustomer(customerId: Integer, jwtToken:String): Unit

  def updateCustomer(customer: CustomerDto, jwtToken:String): CustomerDto
}
