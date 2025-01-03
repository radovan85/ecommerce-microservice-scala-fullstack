package com.radovan.spring.entity

import jakarta.persistence.{Entity, Table, Id, GeneratedValue, GenerationType, Column}
import scala.beans.BeanProperty

@Entity
@Table(name = "product_categories")
@SerialVersionUID(1L)
class ProductCategoryEntity extends Serializable{

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id")
  @BeanProperty var productCategoryId: Integer = _

  @Column(length = 40, nullable = false,unique = true)
  @BeanProperty var name: String = _
}

