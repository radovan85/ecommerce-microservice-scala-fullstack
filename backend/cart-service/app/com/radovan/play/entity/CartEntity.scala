package com.radovan.play.entity

import java.util
import jakarta.persistence.{Column, Entity, FetchType, GeneratedValue, GenerationType, Id, OneToMany, Table}

import scala.beans.BeanProperty

@Entity
@Table(name = "carts")
@SerialVersionUID(1L)
class CartEntity extends Serializable{

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id")
  @BeanProperty var cartId:Integer = _

  @OneToMany(mappedBy = "cart", fetch = FetchType.EAGER, orphanRemoval = true)
  @BeanProperty var cartItems:util.List[CartItemEntity] = _

  @Column(name = "cart_price", nullable = false)
  @BeanProperty var cartPrice:Float = _
}
