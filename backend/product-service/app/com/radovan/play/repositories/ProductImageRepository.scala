package com.radovan.play.repositories

import com.radovan.play.entity.ProductImageEntity

trait ProductImageRepository {

  def save(imageEntity: ProductImageEntity):ProductImageEntity

  def findByProductId(productId:Integer):Option[ProductImageEntity]

  def findAll:Array[ProductImageEntity]

  def deleteById(imageId:Integer):Unit

  def findById(imageId:Integer):Option[ProductImageEntity]
}
