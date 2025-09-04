package com.radovan.scalatra.services.impl

import com.radovan.scalatra.converter.TempConverter
import com.radovan.scalatra.dto.ShippingAddressDto
import com.radovan.scalatra.exceptions.InstanceUndefinedException
import com.radovan.scalatra.repositories.ShippingAddressRepository
import com.radovan.scalatra.services.ShippingAddressService
import jakarta.inject.{Inject, Singleton}

@Singleton
class ShippingAddressServiceImpl extends ShippingAddressService {

  private var addressRepository:ShippingAddressRepository = _
  private var tempConverter:TempConverter = _

  @Inject
  private def initialize(addressRepository: ShippingAddressRepository,tempConverter: TempConverter):Unit = {
    this.addressRepository = addressRepository
    this.tempConverter = tempConverter
  }

  override def getAddressById(addressId: Integer): ShippingAddressDto = {
    addressRepository.findById(addressId) match {
      case Some(addressEntity) => tempConverter.addressEntityToDto(addressEntity)
      case None => throw new InstanceUndefinedException("The address has not been found!")
    }
  }

  override def updateAddress(address: ShippingAddressDto, addressId: Integer): ShippingAddressDto = {
    val currentAddress = getAddressById(addressId)
    address.setShippingAddressId(currentAddress.getShippingAddressId)
    address.setCustomerId(currentAddress.getCustomerId)
    val updatedAddress = addressRepository.save(tempConverter.addressDtoToEntity(address))
    tempConverter.addressEntityToDto(updatedAddress)
  }

  override def listAll: Array[ShippingAddressDto] = {
    val allAddresses = addressRepository.findAll
    allAddresses.collect{
      case addressEntity => tempConverter.addressEntityToDto(addressEntity)
    }
  }


}
