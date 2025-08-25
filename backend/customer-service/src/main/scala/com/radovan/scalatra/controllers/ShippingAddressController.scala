package com.radovan.scalatra.controllers

import com.radovan.scalatra.dto.ShippingAddressDto
import com.radovan.scalatra.security.{CorsHandler, SecuritySupport}
import com.radovan.scalatra.services.ShippingAddressService
import com.radovan.scalatra.utils.ResponsePackage
import jakarta.inject.Inject
import org.apache.hc.core5.http.HttpStatus
import org.scalatra.ScalatraServlet

class ShippingAddressController extends ScalatraServlet
with CorsHandler
with SecuritySupport
with ErrorsController {

  private var addressService:ShippingAddressService = _

  @Inject
  private def initialize(addressService: ShippingAddressService):Unit = {
    this.addressService = addressService
  }

  get("/"){
    secured(Set("ROLE_ADMIN")) {
      new ResponsePackage[Array[ShippingAddressDto]](addressService.listAll,HttpStatus.SC_OK).toResponse(response)
    }
  }
}
