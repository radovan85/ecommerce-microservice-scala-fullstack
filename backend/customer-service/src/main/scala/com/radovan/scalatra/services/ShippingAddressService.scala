package com.radovan.scalatra.services

import com.radovan.scalatra.dto.ShippingAddressDto

trait ShippingAddressService {

  def getAddressById(addressId:Integer):ShippingAddressDto
  def updateAddress(address:ShippingAddressDto,addressId:Integer):ShippingAddressDto
  def listAll:Array[ShippingAddressDto]

}
