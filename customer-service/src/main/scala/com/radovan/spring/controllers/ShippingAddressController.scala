package com.radovan.spring.controllers

import com.radovan.spring.dto.ShippingAddressDto
import com.radovan.spring.exceptions.DataNotValidatedException
import com.radovan.spring.services.ShippingAddressService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.Errors
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation._
import org.springframework.web.context.request.{RequestAttributes, RequestContextHolder}

@RestController
@RequestMapping(Array("/addresses"))
class ShippingAddressController {

  @Autowired
  private var addressService: ShippingAddressService = _

  private def setAuthorizationHeader(authorizationHeader: String): Unit = {
    RequestContextHolder.getRequestAttributes.setAttribute("Authorization", authorizationHeader, RequestAttributes.SCOPE_REQUEST)
  }

  @PreAuthorize("hasAuthority('ROLE_USER')")
  @PutMapping(Array("/updateAddress"))
  def updateAddress(
                     @RequestHeader(value = "Authorization", required = false) authorizationHeader: String,
                     @RequestBody @Validated address: ShippingAddressDto,
                     errors: Errors
                   ): ResponseEntity[String] = {
    setAuthorizationHeader(authorizationHeader)
    if (errors.hasErrors) {
      throw new DataNotValidatedException(new Error("The address has not been validated!"))
    }
    addressService.updateAddress(address)
    new ResponseEntity[String]("Your shipping address has been updated!", HttpStatus.OK)
  }

  @GetMapping(Array("/addressDetails/{addressId}"))
  def getAddressDetails(
                         @RequestHeader(value = "Authorization", required = false) authorizationHeader: String,
                         @PathVariable("addressId") addressId: Int
                       ): ResponseEntity[ShippingAddressDto] = {
    new ResponseEntity[ShippingAddressDto](addressService.getAddressById(addressId), HttpStatus.OK)
  }
}

