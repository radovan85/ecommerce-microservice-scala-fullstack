package com.radovan.spring.services.impl

import com.radovan.spring.converter.TempConverter
import com.radovan.spring.dto.ShippingAddressDto
import com.radovan.spring.exceptions.InstanceUndefinedException
import com.radovan.spring.repositories.ShippingAddressRepository
import com.radovan.spring.services.{CustomerService, ShippingAddressService}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ShippingAddressServiceImpl extends ShippingAddressService {

  private var addressRepository: ShippingAddressRepository = _
  private var tempConverter: TempConverter = _
  private var customerService: CustomerService = _

  @Autowired
  private def initialize(addressRepository: ShippingAddressRepository, tempConverter: TempConverter,
                         customerService: CustomerService): Unit = {
    this.addressRepository = addressRepository
    this.tempConverter = tempConverter
    this.customerService = customerService
  }

  @Transactional
  override def addAddress(address: ShippingAddressDto): ShippingAddressDto = {
    val storedAddress = addressRepository.save(tempConverter.addressDtoToEntity(address))
    tempConverter.addressEntityToDto(storedAddress)
  }

  @Transactional
  override def updateAddress(address: ShippingAddressDto): ShippingAddressDto = {
    val currentCustomer = customerService.getCurrentCustomer
    val currentAddress = getAddressById(currentCustomer.getShippingAddressId)
    address.setShippingAddressId(currentAddress.getShippingAddressId)
    address.setCustomerId(currentCustomer.getCustomerId)
    val updatedAddress = addressRepository.saveAndFlush(tempConverter.addressDtoToEntity(address))
    tempConverter.addressEntityToDto(updatedAddress)
  }

  @Transactional(readOnly = true)
  override def getAddressById(addressId: Integer): ShippingAddressDto = {
    val addressEntity = addressRepository.findById(addressId)
      .orElseThrow(() => new InstanceUndefinedException(new Error("The shipping address has not ben found!")))
    tempConverter.addressEntityToDto(addressEntity)
  }
}
