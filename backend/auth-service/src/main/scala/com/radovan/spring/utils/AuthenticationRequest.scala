package com.radovan.spring.utils

import scala.beans.BeanProperty

@SerialVersionUID(1L)
class AuthenticationRequest extends Serializable {

  @BeanProperty
  var username: String = _

  @BeanProperty
  var password: String = _
}
