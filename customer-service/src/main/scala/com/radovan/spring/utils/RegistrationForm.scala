package com.radovan.spring.utils

import com.fasterxml.jackson.databind.JsonNode
import com.radovan.spring.dto.{CustomerDto, ShippingAddressDto}

import scala.beans.BeanProperty

@SerialVersionUID(1L)
class RegistrationForm extends Serializable{

  @BeanProperty var user:JsonNode = _

  @BeanProperty var customer:CustomerDto = _

  @BeanProperty var address:ShippingAddressDto = _

}
