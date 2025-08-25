package com.radovan.play.repositories

import com.radovan.play.entity.OrderAddressEntity

trait OrderAddressRepository {

  def findById(addressId:Integer):Option[OrderAddressEntity]

  def findAll:Array[OrderAddressEntity]

  def save(addressEntity: OrderAddressEntity):OrderAddressEntity
}
