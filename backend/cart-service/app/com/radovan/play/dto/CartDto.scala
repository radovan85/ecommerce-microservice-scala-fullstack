package com.radovan.play.dto

import scala.beans.BeanProperty

@SerialVersionUID(1L)
class CartDto extends Serializable{

  @BeanProperty var cartId:Integer = _
  @BeanProperty var cartItemsIds:Array[Integer] = _
  @BeanProperty var cartPrice:Float = _
}
