package com.radovan.scalatra.converter

import com.radovan.scalatra.dto.{CustomerDto, ShippingAddressDto}
import com.radovan.scalatra.entity.{CustomerEntity, ShippingAddressEntity}
import com.radovan.scalatra.repositories.{CustomerRepository, ShippingAddressRepository}
import jakarta.inject.{Inject, Singleton}
import org.modelmapper.ModelMapper

@Singleton
class TempConverter {

  private var mapper:ModelMapper = _
  private var addressRepository:ShippingAddressRepository = _
  private var customerRepository:CustomerRepository = _

  @Inject
  private def initialize(mapper: ModelMapper,addressRepository: ShippingAddressRepository,
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
        addressRepository.findById(addressId) match {
          case Some(addressEntity) => returnValue.setShippingAddress(addressEntity)
          case None =>
        }
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
        customerRepository.findById(customerId) match {
          case Some(customerEntity) => returnValue.setCustomer(customerEntity)
          case None =>
        }

      case None =>
    }

    returnValue
  }
}
