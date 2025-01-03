package com.radovan.spring.services

import com.radovan.spring.dto.ShippingAddressDto

trait ShippingAddressService {

  def addAddress(address:ShippingAddressDto):ShippingAddressDto

  def updateAddress(address:ShippingAddressDto):ShippingAddressDto

  def getAddressById(addressId:Integer):ShippingAddressDto
}
