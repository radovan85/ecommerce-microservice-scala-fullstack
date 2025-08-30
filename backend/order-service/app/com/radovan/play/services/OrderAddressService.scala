package com.radovan.play.services

import com.radovan.play.dto.OrderAddressDto

trait OrderAddressService {

  def listAll:Array[OrderAddressDto]
}
