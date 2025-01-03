package com.radovan.spring.dto

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

import scala.beans.BeanProperty


@SerialVersionUID(1L)
class ShippingAddressDto extends Serializable {

  @BeanProperty var shippingAddressId:Integer = _

  @NotEmpty
  @Size(max = 75)
  @BeanProperty var address:String = _

  @NotEmpty
  @Size(max = 40)
  @BeanProperty var city:String = _

  @NotEmpty
  @Size(max = 40)
  @BeanProperty var state:String = _

  @NotEmpty
  @Size(max = 40)
  @BeanProperty var country:String = _

  @NotEmpty
  @Size(min = 5, max = 10)
  @BeanProperty var postcode:String = _

  @BeanProperty var customerId:Integer = _


}

