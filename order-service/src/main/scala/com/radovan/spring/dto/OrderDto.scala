package com.radovan.spring.dto

import jakarta.validation.constraints.NotNull

import scala.beans.BeanProperty

@SerialVersionUID(1L)
class OrderDto extends Serializable {

  @BeanProperty var orderId: Integer = _

  @NotNull
  @BeanProperty var orderPrice: Float = _

  @NotNull
  @BeanProperty var cartId: Integer = _

  @BeanProperty var createdAt: String = _

  @BeanProperty var orderedItemsIds: Array[Integer] = _

  @NotNull
  @BeanProperty var addressId: Integer = _

}
