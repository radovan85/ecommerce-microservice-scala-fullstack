package com.radovan.play.entity

import java.util
import jakarta.persistence.{Column, Entity, FetchType, GeneratedValue, GenerationType, Id, JoinColumn, OneToMany, OneToOne, Table}

import java.sql.Timestamp
import scala.beans.BeanProperty

@Entity
@Table(name="orders")
@SerialVersionUID(1L)
class OrderEntity extends Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id")
  @BeanProperty var orderId:Integer = _

  @Column(name = "order_price", nullable = false)
  @BeanProperty var orderPrice:Float = _

  @Column(name = "cart_id", nullable = false)
  @BeanProperty var cartId:Integer = _

  @Column(name = "created_time", nullable = false)
  @BeanProperty var createTime:Timestamp = _

  @OneToMany(mappedBy = "order", fetch = FetchType.EAGER, orphanRemoval = true)
  @BeanProperty var orderedItems:util.List[OrderItemEntity] = _

  @OneToOne(orphanRemoval = true, fetch = FetchType.EAGER)
  @JoinColumn(name = "address_id", nullable = false)
  @BeanProperty var address:OrderAddressEntity = _
}
