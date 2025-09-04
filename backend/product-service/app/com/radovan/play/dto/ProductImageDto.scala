package com.radovan.play.dto

import scala.beans.BeanProperty
import jakarta.validation.constraints.NotNull

@SerialVersionUID(1L)
class ProductImageDto extends Serializable {

  @BeanProperty var id: Integer = _

  @BeanProperty var name: String = _

  @BeanProperty var contentType: String = _

  @BeanProperty var size: Long = _

  @BeanProperty var data: Array[Byte] = _

  @NotNull
  @BeanProperty var productId: Integer = _

}

