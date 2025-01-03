package com.radovan.spring.dto

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

import scala.beans.BeanProperty


@SerialVersionUID(1L)
class CustomerDto extends Serializable {

  @BeanProperty var customerId: Integer = _

  @NotEmpty
  @Size(min = 9, max = 15)
  @BeanProperty var customerPhone: String = _

  @BeanProperty var shippingAddressId: Integer = _

  @BeanProperty var userId: Integer = _

  @BeanProperty var cartId: Integer = _

}
