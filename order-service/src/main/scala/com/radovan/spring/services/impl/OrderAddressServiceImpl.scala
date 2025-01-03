package com.radovan.spring.services.impl

import com.radovan.spring.converter.TempConverter
import com.radovan.spring.dto.OrderAddressDto
import com.radovan.spring.repositories.OrderAddressRepository
import com.radovan.spring.services.OrderAddressService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import scala.jdk.CollectionConverters._

@Service
class OrderAddressServiceImpl extends OrderAddressService {

  private var addressRepository:OrderAddressRepository = _
  private var tempConverter:TempConverter = _

  @Autowired
  private def initialize(addressRepository: OrderAddressRepository,tempConverter: TempConverter):Unit = {
    this.addressRepository = addressRepository
    this.tempConverter = tempConverter
  }

  @Transactional(readOnly = true)
  override def listAll: Array[OrderAddressDto] = {
    val allAddresses = addressRepository.findAll().asScala
    allAddresses.collect{
      case addressEntity => tempConverter.orderAddressEntityToDto(addressEntity)
    }.toArray
  }
}
