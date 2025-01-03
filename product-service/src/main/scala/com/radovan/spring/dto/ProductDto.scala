package com.radovan.spring.dto

import scala.beans.BeanProperty
import jakarta.validation.constraints.{DecimalMin, Min, NotEmpty, NotNull, Size}

@SerialVersionUID(1L)
class ProductDto extends Serializable {

  @BeanProperty var productId: Integer = _

  @NotEmpty
  @Size(max = 100)
  @BeanProperty var productDescription: String = _

  @NotEmpty
  @Size(max = 40)
  @BeanProperty var productBrand: String = _

  @NotEmpty
  @Size(max = 40)
  @BeanProperty var productModel: String = _

  @NotEmpty
  @Size(max = 40)
  @BeanProperty var productName: String = _

  @NotNull
  @DecimalMin(value = "1.00")
  @BeanProperty var productPrice: Float = _

  @NotNull
  @Min(value = 0)
  @BeanProperty var unitStock: Integer = _

  @NotNull
  @DecimalMin(value = "0.00")
  @BeanProperty var discount: Float = _

  @BeanProperty var imageId: Integer = _

  @NotNull
  @BeanProperty var productCategoryId: Integer = _

}

