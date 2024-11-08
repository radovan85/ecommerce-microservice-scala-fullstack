package com.radovan.spring.dto

import scala.beans.BeanProperty
import jakarta.validation.constraints.{NotEmpty, Size}

@SerialVersionUID(1L)
class RoleDto extends Serializable {

  @BeanProperty var id: Integer = _

  @BeanProperty
  @NotEmpty
  @Size(max = 30) var role: String = _

  @BeanProperty var usersIds: Array[Integer] = _

}

