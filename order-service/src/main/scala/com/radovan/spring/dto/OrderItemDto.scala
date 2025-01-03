package com.radovan.spring.dto

import jakarta.validation.constraints.{DecimalMin, NotEmpty, NotNull}

import scala.beans.BeanProperty

@SerialVersionUID(1L)
class OrderItemDto extends Serializable {

  @BeanProperty var orderItemId: Integer = _

  @NotNull
  @BeanProperty var quantity: Integer = _

  @NotNull
  @BeanProperty var price: Float = _

  @NotEmpty
  @BeanProperty var productName: String = _

  @NotNull
  @DecimalMin(value = "0.00")
  @BeanProperty var productDiscount: Float = _

  @NotNull
  @DecimalMin(value = "1.00")
  @BeanProperty var productPrice: Float = _

  @NotNull
  @BeanProperty var orderId: Integer = _
}
