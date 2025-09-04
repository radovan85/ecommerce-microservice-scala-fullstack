package com.radovan.play.dto

import jakarta.validation.constraints.{NotEmpty, Size}

import scala.beans.BeanProperty

@SerialVersionUID(1L)
class ProductCategoryDto extends Serializable {

  @BeanProperty var productCategoryId: Integer = _

  @NotEmpty
  @Size(min = 2, max = 40)
  @BeanProperty var name: String = _
}
