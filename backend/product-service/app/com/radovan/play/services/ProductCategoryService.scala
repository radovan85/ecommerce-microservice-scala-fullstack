package com.radovan.play.services

import com.radovan.play.dto.ProductCategoryDto

trait ProductCategoryService {

  def addCategory(categoryDto: ProductCategoryDto):ProductCategoryDto

  def getCategoryById(categoryId:Int):ProductCategoryDto

  def updateCategory(categoryDto: ProductCategoryDto,categoryId:Int):ProductCategoryDto

  def deleteCategory(categoryId:Int,jwtToken:String):Unit

  def listAll:Array[ProductCategoryDto]
}
