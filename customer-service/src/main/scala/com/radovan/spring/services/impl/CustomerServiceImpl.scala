package com.radovan.spring.services.impl

import com.fasterxml.jackson.databind.JsonNode
import com.radovan.spring.converter.TempConverter
import com.radovan.spring.dto.CustomerDto
import com.radovan.spring.exceptions.InstanceUndefinedException
import com.radovan.spring.repositories.CustomerRepository
import com.radovan.spring.services.{CustomerService, ShippingAddressService}
import com.radovan.spring.utils.{RegistrationForm, ServiceUrlProvider}
import flexjson.JSONDeserializer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpEntity, HttpMethod}
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate

import java.util
import scala.jdk.CollectionConverters._

@Service
class CustomerServiceImpl extends CustomerService {

  private var restTemplate:RestTemplate = _
  private var addressService:ShippingAddressService = _
  private var customerRepository:CustomerRepository = _
  private var tempConverter:TempConverter = _
  private var urlProvider:ServiceUrlProvider = _

  @Autowired
  private def initialize(restTemplate: RestTemplate,addressService: ShippingAddressService,
                         customerRepository: CustomerRepository,tempConverter: TempConverter,
                         urlProvider: ServiceUrlProvider):Unit = {
    this.restTemplate = restTemplate
    this.addressService = addressService
    this.customerRepository = customerRepository
    this.tempConverter = tempConverter
    this.urlProvider = urlProvider
  }

  @Transactional
  override def addCustomer(form: RegistrationForm): CustomerDto = {
    val storedAddress = addressService.addAddress(form.getAddress)
    val customer = form.getCustomer
    customer.setShippingAddressId(storedAddress.getShippingAddressId)
    val user = form.getUser
    val userRequestEntity = new HttpEntity[JsonNode](user)
    val userServiceUrl = s"${urlProvider.getUserServiceUrl}/addUser"
    val response = restTemplate.exchange(userServiceUrl, HttpMethod.POST, userRequestEntity, classOf[JsonNode])

    val cartServiceUrl = s"${urlProvider.getCartServiceUrl}/cart/addCart"
    val cartResponse = restTemplate.exchange(cartServiceUrl, HttpMethod.POST, null, classOf[JsonNode])

    if (cartResponse.getStatusCode.is2xxSuccessful && cartResponse.getBody != null) {
      val cartId = cartResponse.getBody.get("cartId").asInt()
      customer.setCartId(cartId)
    } else {
      throw new InstanceUndefinedException(new Error("Cart id not found for customer"))
    }

    if (response.getStatusCode.is2xxSuccessful) {
      val responseBody = response.getBody
      val userId = new JSONDeserializer[Integer]()
        .deserialize(responseBody.get("id").toString, classOf[Integer])
      customer.setUserId(userId)
      customer.setShippingAddressId(storedAddress.getShippingAddressId)

      val storedCustomer = customerRepository.save(tempConverter.customerDtoToEntity(customer))
      storedAddress.setCustomerId(storedCustomer.getCustomerId)
      addressService.addAddress(storedAddress)

      tempConverter.customerEntityToDto(storedCustomer)
    } else {

      null
    }
  }

  @Transactional(readOnly = true)
  override def listAll: Array[CustomerDto] = {
    val allCustomers = customerRepository.findAll.asScala
    allCustomers.collect{
      case customerEntity => tempConverter.customerEntityToDto(customerEntity)
    }.toArray
  }

  @Transactional(readOnly = true)
  override def getCustomerById(customerId: Integer): CustomerDto = {
    val customerEntity = customerRepository.findById(customerId)
      .orElseThrow(() => new InstanceUndefinedException(new Error("The customer has not been found!")))
    tempConverter.customerEntityToDto(customerEntity)
  }

  @Transactional(readOnly = true)
  override def getCustomerByUserId(userId: Integer): CustomerDto = {
    val customerOption = customerRepository.findByUserId(userId)
    customerOption match {
      case Some(customerEntity) => tempConverter.customerEntityToDto(customerEntity)
      case None => throw new InstanceUndefinedException(new Error("The customer has not been found!"))
    }
  }

  @Transactional
  override def removeCustomer(customerId: Integer): Unit = {
    val customer = getCustomerById(customerId)
    customerRepository.deleteById(customerId)
    customerRepository.flush()
    val userServiceUrl = s"${urlProvider.getUserServiceUrl}/deleteUser/${customer.getUserId}"
    restTemplate.exchange(userServiceUrl, HttpMethod.DELETE, null, classOf[String])
    val deleteOrdersUrl = s"${urlProvider.getOrderServiceUrl}/order/deleteAllByCartId/${customer.getCartId}"
    restTemplate.exchange(deleteOrdersUrl, HttpMethod.DELETE, null, classOf[Unit])
    val deleteCartUrl = s"${urlProvider.getCartServiceUrl}/cart/deleteCart/${customer.getCartId}"
    restTemplate.exchange(deleteCartUrl, HttpMethod.DELETE, null, classOf[Unit])
  }

  @Transactional(readOnly = true)
  def getCurrentCustomer: CustomerDto = {
    val userUrl = s"${urlProvider.getUserServiceUrl}/currentUser"

    // Dobavljanje korisnika sa REST servisa
    val userResponse: JsonNode = restTemplate.getForObject(userUrl, classOf[JsonNode])
    if (userResponse == null) {
      throw new InstanceUndefinedException(new Error("The user has not been found!."))
    }

    // Konverzija JSON-a u Map (koristeći Java stil)
    val userJsonString = userResponse.toString
    val userMap: util.Map[String, Any] = tempConverter.deserializeJson(userJsonString)
    if (userMap == null || !userMap.containsKey("id")) {
      throw new InstanceUndefinedException(new Error("User id not found!"))
    }

    // Parsiranje ID-a korisnika
    val userIdStr = userMap.get("id").toString
    val userId = try {
      Integer.parseInt(userIdStr)
    } catch {
      case _: NumberFormatException =>
        throw new InstanceUndefinedException(new Error("User id is not valid: " + userIdStr))
    }

    // Vraćanje kupca na osnovu userId
    getCustomerByUserId(userId)
  }

  @Transactional
  override def updateCustomerCartId(customerId: Integer, cartId: Integer): Unit = {
    val customerEntity = customerRepository.findById(customerId)
      .orElseThrow(() => new InstanceUndefinedException(new Error("The customer has not been found!")))
    customerEntity.setCartId(cartId)
    customerRepository.save(customerEntity)
  }

  @Transactional
  override def suspendCustomer(customerId: Integer): Unit = {
    val customer = getCustomerById(customerId)
    val url = s"${urlProvider.getUserServiceUrl}/suspendUser/${customer.getUserId}"
    restTemplate.exchange(url,HttpMethod.PUT,null,classOf[Unit])
  }

  @Transactional
  override def reactivateCustomer(customerId: Integer): Unit = {
    val customer = getCustomerById(customerId)
    val url = s"${urlProvider.getUserServiceUrl}/reactivateUser/${customer.getUserId}"
    restTemplate.exchange(url, HttpMethod.PUT, null, classOf[Unit])
  }
}
