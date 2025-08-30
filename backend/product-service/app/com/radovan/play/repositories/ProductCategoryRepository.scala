package com.radovan.play.repositories

import com.radovan.play.entity.ProductCategoryEntity

trait ProductCategoryRepository {

  def findAll:Array[ProductCategoryEntity]

  def deleteById(categoryId:Integer):Unit

  def findById(categoryId:Integer):Option[ProductCategoryEntity]

  def save(categoryEntity: ProductCategoryEntity):ProductCategoryEntity

  def findByName(name:String):Option[ProductCategoryEntity]
}
