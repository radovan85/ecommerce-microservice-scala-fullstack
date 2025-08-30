package com.radovan.play.dto

import jakarta.validation.constraints.{DecimalMin, NotEmpty, NotNull, PositiveOrZero, Size}

import scala.beans.BeanProperty

@SerialVersionUID(1L)
class ProductDto extends Serializable{

  @BeanProperty var productId:Integer = _

  @NotEmpty
  @Size(min = 5, max = 100)
  @BeanProperty var productDescription:String = _

  @NotEmpty
  @Size(min = 2, max = 40)
  @BeanProperty var productBrand:String = _

  @NotEmpty
  @Size(min = 2, max = 40)
  @BeanProperty var productModel:String = _

  @NotEmpty
  @Size(min = 2, max = 40)
  @BeanProperty var productName:String = _

  @NotNull
  @DecimalMin(value="1.00")
  @BeanProperty var productPrice:Float = _

  @NotNull
  @PositiveOrZero
  @BeanProperty var unitStock:Integer = _

  @NotNull
  @PositiveOrZero
  @BeanProperty var discount:Float = _

  @BeanProperty var imageId:Integer = _

  @NotNull
  @PositiveOrZero
  @BeanProperty var productCategoryId:Integer = _
}
