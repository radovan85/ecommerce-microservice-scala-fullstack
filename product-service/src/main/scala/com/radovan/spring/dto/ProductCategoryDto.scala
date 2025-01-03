package com.radovan.spring.dto

import scala.beans.BeanProperty
import jakarta.validation.constraints.{NotEmpty, Size}

@SerialVersionUID(1L)
class ProductCategoryDto extends Serializable {

  @BeanProperty var productCategoryId: Integer = _

  @NotEmpty
  @Size(max = 40, min = 2)
  @BeanProperty var name: String = _

}
