package com.radovan.spring.entity

import jakarta.persistence.{CascadeType, Column, Entity, FetchType, GeneratedValue, GenerationType, Id, JoinColumn, ManyToOne, OneToOne, Table}

import scala.beans.BeanProperty

@Entity
@Table(name = "products")
@SerialVersionUID(1L)
class ProductEntity extends Serializable{

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.AUTO)
  @BeanProperty var productId: Integer = _

  @Column(name = "description", nullable = false, length = 100)
  @BeanProperty var productDescription: String = _

  @Column(name = "product_brand", nullable = false, length = 40)
  @BeanProperty var productBrand: String = _

  @Column(name = "product_model", nullable = false, length = 40)
  @BeanProperty var productModel: String = _

  @Column(name = "product_name", nullable = false, length = 40)
  @BeanProperty var productName: String = _

  @Column(name = "price", nullable = false)
  @BeanProperty var productPrice: Float = _

  @Column(name = "unit", nullable = false)
  @BeanProperty var unitStock: Integer = _

  @Column(nullable = false)
  @BeanProperty var discount: Float = _

  @OneToOne(fetch = FetchType.EAGER, mappedBy = "product",orphanRemoval = true)
  @BeanProperty var image: ProductImageEntity = _

  @ManyToOne
  @JoinColumn(name = "category_id")
  @BeanProperty var productCategory: ProductCategoryEntity = _
}

