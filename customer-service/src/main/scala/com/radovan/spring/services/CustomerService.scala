package com.radovan.spring.services

import com.radovan.spring.dto.CustomerDto
import com.radovan.spring.utils.RegistrationForm

trait CustomerService {

  def addCustomer(form:RegistrationForm):CustomerDto

  def listAll:Array[CustomerDto]

  def getCustomerById(customerId:Integer):CustomerDto

  def getCustomerByUserId(userId:Integer):CustomerDto

  def removeCustomer(customerId:Integer):Unit

  def getCurrentCustomer:CustomerDto

  def updateCustomerCartId(customerId:Integer,cartId:Integer):Unit

  def suspendCustomer(customerId:Integer):Unit

  def reactivateCustomer(customerId:Integer):Unit


}
