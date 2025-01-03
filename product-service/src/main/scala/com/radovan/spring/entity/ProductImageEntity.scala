package com.radovan.spring.entity

import jakarta.persistence.{Column, Entity, FetchType, GeneratedValue, GenerationType, Id, JoinColumn, Lob, OneToOne, Table}

import scala.beans.BeanProperty

@Entity
@Table(name = "product_images")
@SerialVersionUID(1L)
class ProductImageEntity extends Serializable{

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @BeanProperty var id: Integer = _

  @BeanProperty var name: String = _

  @Column(name = "content_type")
  @BeanProperty var contentType: String = _

  @BeanProperty var size: Long = _

  @Lob
  @BeanProperty var data: Array[Byte] = _

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "product_id", nullable = false)
  @BeanProperty var product: ProductEntity = _
}
