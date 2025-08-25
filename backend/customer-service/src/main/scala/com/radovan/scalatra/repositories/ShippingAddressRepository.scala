package com.radovan.scalatra.repositories

import com.radovan.scalatra.entity.ShippingAddressEntity

trait ShippingAddressRepository {

  def findById(addressId: Integer):Option[ShippingAddressEntity]

  def save(addressEntity: ShippingAddressEntity):ShippingAddressEntity

  def findAll:Array[ShippingAddressEntity]

}
