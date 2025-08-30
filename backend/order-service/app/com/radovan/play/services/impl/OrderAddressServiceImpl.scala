package com.radovan.play.services.impl

import com.radovan.play.converter.TempConverter
import com.radovan.play.dto.OrderAddressDto
import com.radovan.play.repositories.OrderAddressRepository
import com.radovan.play.services.OrderAddressService
import jakarta.inject.{Inject, Singleton}

@Singleton
class OrderAddressServiceImpl extends OrderAddressService{

  private var addressRepository:OrderAddressRepository = _
  private var tempConverter:TempConverter = _

  @Inject
  private def initialize(addressRepository: OrderAddressRepository,tempConverter: TempConverter):Unit = {
    this.addressRepository = addressRepository
    this.tempConverter = tempConverter
  }

  override def listAll: Array[OrderAddressDto] = {
    addressRepository.findAll.collect{
      case addressEntity => tempConverter.orderAddressEntityToDto(addressEntity)
    }
  }
}
