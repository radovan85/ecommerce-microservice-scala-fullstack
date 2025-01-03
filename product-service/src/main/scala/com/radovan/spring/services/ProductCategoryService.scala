package com.radovan.spring.services

import com.radovan.spring.dto.ProductCategoryDto

trait ProductCategoryService {

  def addCategory(category:ProductCategoryDto):ProductCategoryDto

  def getCategoryById(categoryId:Integer):ProductCategoryDto

  def updateCategory(category:ProductCategoryDto,categoryId:Integer):ProductCategoryDto

  def deleteCategory(categoryId:Integer):Unit

  def listAll:Array[ProductCategoryDto]
}
