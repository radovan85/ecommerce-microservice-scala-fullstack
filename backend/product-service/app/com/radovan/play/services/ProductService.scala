package com.radovan.play.services

import com.radovan.play.dto.ProductDto

trait ProductService {

  def addProduct(productDto: ProductDto):ProductDto

  def getProductById(productId:Int):ProductDto

  def updateProduct(productDto: ProductDto,productId:Int,jwtToken:String):ProductDto

  def deleteProduct(productId:Int,jwtToken:String):Unit

  def listAll:Array[ProductDto]

  def listAllByCategoryId(categoryId:Int):Array[ProductDto]

  def deleteProductsByCategoryId(categoryId:Int,jwtToken:String):Unit
}
