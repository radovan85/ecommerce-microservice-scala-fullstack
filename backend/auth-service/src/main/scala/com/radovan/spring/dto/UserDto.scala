package com.radovan.spring.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonProperty.Access
import scala.beans.BeanProperty
import jakarta.validation.constraints.{Email, NotEmpty, Size}


@SerialVersionUID(1L)
class UserDto extends Serializable {

  @BeanProperty var id: Integer = _

  @NotEmpty
  @Size(min = 2, max = 30)
  @BeanProperty var firstName: String = _

  @NotEmpty
  @Size(min = 2, max = 30)
  @BeanProperty var lastName: String = _


  @NotEmpty
  @Size(max = 50)
  @Email(regexp = ".+[@].+[\\.].+")
  @BeanProperty var email: String = _


  @NotEmpty
  @Size(min = 6)
  @JsonProperty(access = Access.WRITE_ONLY)
  @BeanProperty var password: String = _

  @BeanProperty var enabled: Short = _

  @BeanProperty var rolesIds: Array[Integer] = _

  @BeanProperty var authToken: String = _

  override def toString: String = s"UserDto [firstName=$firstName, lastName=$lastName, email=$email, password=$password, enabled=$enabled]"

}