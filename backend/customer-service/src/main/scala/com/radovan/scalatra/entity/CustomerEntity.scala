package com.radovan.scalatra.entity

import jakarta.persistence._

import scala.beans.BeanProperty

@Entity
@Table(name = "customers")
@SerialVersionUID(1L)
class CustomerEntity extends Serializable{

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  @BeanProperty var customerId:Integer = _

  @Column(name = "phone", nullable = false, length = 15)
  @BeanProperty var customerPhone:String = _

  @OneToOne(fetch = FetchType.EAGER, orphanRemoval = true)
  @JoinColumn(name = "shipping_address_id", nullable = false)
  @BeanProperty var shippingAddress:ShippingAddressEntity = _

  @Column(name = "user_id", nullable = false)
  @BeanProperty var userId:Integer = _

  @Column(name = "cart_id", nullable = false)
  @BeanProperty var cartId:Integer = _
}