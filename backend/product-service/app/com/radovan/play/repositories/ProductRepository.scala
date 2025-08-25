package com.radovan.play.repositories

import com.radovan.play.entity.ProductEntity

trait ProductRepository {

  def findById(productId:Integer):Option[ProductEntity]

  def save(productEntity: ProductEntity):ProductEntity

  def deleteById(productId:Integer):Unit

  def findAll:Array[ProductEntity]

  def findAllByCategoryId(categoryId:Integer):Array[ProductEntity]
}
