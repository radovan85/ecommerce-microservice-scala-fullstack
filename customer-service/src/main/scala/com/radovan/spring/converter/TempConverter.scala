package com.radovan.spring.converter

import java.util

import com.radovan.spring.dto.{CustomerDto, ShippingAddressDto}
import com.radovan.spring.entity.{CustomerEntity, ShippingAddressEntity}
import com.radovan.spring.repositories.{CustomerRepository, ShippingAddressRepository}
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import flexjson.JSONDeserializer

@Component
class TempConverter {

  private var mapper:ModelMapper = _
  private var addressRepository:ShippingAddressRepository = _
  private var customerRepository:CustomerRepository = _

  @Autowired
  private def initialize(mapper:ModelMapper,addressRepository: ShippingAddressRepository,
                         customerRepository: CustomerRepository):Unit = {
    this.mapper = mapper
    this.addressRepository = addressRepository
    this.customerRepository = customerRepository
  }

  def customerEntityToDto(customer:CustomerEntity):CustomerDto = {
    val returnValue = mapper.map(customer,classOf[CustomerDto])
    val addressOption = Option(customer.getShippingAddress)
    if(addressOption.isDefined) returnValue.setShippingAddressId(addressOption.get.getShippingAddressId)
    returnValue
  }

  def customerDtoToEntity(customer:CustomerDto):CustomerEntity = {
    val returnValue = mapper.map(customer, classOf[CustomerEntity])
    val addressIdOption = Option(customer.getShippingAddressId)
    addressIdOption match {
      case Some(addressId) =>
        val addressEntity = addressRepository.findById(addressId).orElse(null)
        if(addressEntity!=null) returnValue.setShippingAddress(addressEntity)
      case None =>
    }

    returnValue
  }

  def addressEntityToDto(address:ShippingAddressEntity):ShippingAddressDto = {
    val returnValue = mapper.map(address, classOf[ShippingAddressDto])
    val customerOption = Option(address.getCustomer)
    if(customerOption.isDefined) returnValue.setCustomerId(customerOption.get.getCustomerId)
    returnValue
  }

  def addressDtoToEntity(address:ShippingAddressDto):ShippingAddressEntity = {
    val returnValue = mapper.map(address, classOf[ShippingAddressEntity])
    val customerIdOption = Option(address.getCustomerId)
    customerIdOption match {
      case Some(customerId) =>
        val customerEntity = customerRepository.findById(customerId).orElse(null)
        if(customerEntity!=null) returnValue.setCustomer(customerEntity)
      case None =>
    }

    returnValue
  }

  def deserializeJson(jsonString: String): util.Map[String, Any] = {
    val deserializer = new JSONDeserializer[util.Map[String, Any]]()
    deserializer.deserialize(jsonString)
  }
}
