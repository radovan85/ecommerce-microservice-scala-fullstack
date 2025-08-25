package com.radovan.scalatra.services.impl

import com.radovan.scalatra.brokers.CustomerNatsSender
import com.radovan.scalatra.converter.TempConverter
import com.radovan.scalatra.dto.CustomerDto
import com.radovan.scalatra.exceptions.InstanceUndefinedException
import com.radovan.scalatra.repositories.{CustomerRepository, ShippingAddressRepository}
import com.radovan.scalatra.services.CustomerService
import com.radovan.scalatra.utils.RegistrationForm
import jakarta.inject.{Inject, Singleton}

@Singleton
class CustomerServiceImpl extends CustomerService {

  private var customerRepository: CustomerRepository = _
  private var tempConverter: TempConverter = _
  private var natsSender: CustomerNatsSender = _
  private var addressRepository: ShippingAddressRepository = _

  @Inject
  private def initialize(customerRepository: CustomerRepository, tempConverter: TempConverter,
                         natsSender: CustomerNatsSender, addressRepository: ShippingAddressRepository): Unit = {
    this.customerRepository = customerRepository
    this.tempConverter = tempConverter
    this.natsSender = natsSender
    this.addressRepository = addressRepository
  }


  override def addCustomer(form: RegistrationForm): CustomerDto = {
    val userPayload = form.getUser
    val userId = natsSender.sendUserCreate(userPayload)
    val storedAddress = addressRepository.save(tempConverter.addressDtoToEntity(form.getShippingAddress))
    val customer = form.getCustomer
    val cartId: Int = natsSender.sendCartCreate()
    customer.setUserId(userId)
    customer.setShippingAddressId(storedAddress.getShippingAddressId)
    customer.setCartId(cartId)
    val storedCustomer = customerRepository.save(tempConverter.customerDtoToEntity(customer))
    storedAddress.setCustomer(storedCustomer)
    addressRepository.save(storedAddress)
    tempConverter.customerEntityToDto(storedCustomer)
  }

  override def getCustomerById(customerId: Integer): CustomerDto = {
    customerRepository.findById(customerId) match {
      case Some(customerEntity) => tempConverter.customerEntityToDto(customerEntity)
      case None => throw new InstanceUndefinedException("The customer has not been found!")
    }
  }

  override def listAll: Array[CustomerDto] = {
    customerRepository.findAll.collect {
      case customerEntity => tempConverter.customerEntityToDto(customerEntity)
    }
  }

  override def removeCustomer(customerId: Integer, jwtToken:String): Unit = {
    val customer = getCustomerById(customerId)
    customerRepository.deleteById(customerId)
    natsSender.sendDeleteAllOrders(customer.getCartId(),jwtToken)
    natsSender.sendCartDelete(customer.getCartId,jwtToken)
    natsSender.sendDeleteUserEvent(customer.getUserId, jwtToken)
  }

  override def updateCustomer(customer: CustomerDto, jwtToken:String): CustomerDto = {
    val currentCustomer = getCurrentCustomer(jwtToken)
    customer.setCartId(currentCustomer.getCartId)
    customer.setShippingAddressId(currentCustomer.getShippingAddressId)
    customer.setCustomerId(currentCustomer.getCustomerId)
    customer.setUserId(currentCustomer.getUserId)
    val updatedCustomer = customerRepository.save(tempConverter.customerDtoToEntity(customer))
    tempConverter.customerEntityToDto(updatedCustomer)
  }

  override def getCurrentCustomer(jwtToken:String): CustomerDto = {
    val currentUserNode = natsSender.retrieveCurrentUser(jwtToken)
    if (currentUserNode == null || !currentUserNode.has("id")) throw new InstanceUndefinedException("No user ID found in response from NATS!")
    val userId = currentUserNode.get("id").asInt()
    getCustomerByUserId(userId)
  }

  override def suspendCustomer(customerId: Integer, jwtToken:String): Unit = {
    val customer = getCustomerById(customerId)
    natsSender.sendSuspendUserEvent(customer.getUserId, jwtToken)
  }

  override def reactivateCustomer(customerId: Integer, jwtToken:String): Unit = {
    val customer = getCustomerById(customerId)
    natsSender.sendReactivateUserEvent(customer.getUserId, jwtToken)
  }

  override def getCustomerByUserId(userId: Integer): CustomerDto = {
    customerRepository.findByUserId(userId) match {
      case Some(customerEntity) => tempConverter.customerEntityToDto(customerEntity)
      case None => throw new InstanceUndefinedException("The customer has not been found!")
    }
  }
}
