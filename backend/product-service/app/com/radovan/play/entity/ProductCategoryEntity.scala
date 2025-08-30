package com.radovan.play.entity

import jakarta.persistence.{Column, Entity, GeneratedValue, GenerationType, Id, Table}

import scala.beans.BeanProperty

@Entity
@Table(name = "product_categories")
@SerialVersionUID(1L)
class ProductCategoryEntity extends Serializable{

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  @BeanProperty var productCategoryId:Integer = _

  @Column(length = 40, nullable = false,unique = true)
  @BeanProperty var name:String = _
}
