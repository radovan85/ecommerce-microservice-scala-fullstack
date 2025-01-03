package com.radovan.spring.services

import com.fasterxml.jackson.databind.JsonNode
import com.radovan.spring.dto.ProductDto

trait ProductService {

  def addProduct(product:ProductDto):ProductDto

  def getProductById(productId:Integer):ProductDto

  def updateProduct(product:ProductDto,productId:Integer):ProductDto

  def updateProduct(product:JsonNode, productId:Integer):ProductDto

  def deleteProduct(productId:Integer):Unit

  def listAll:Array[ProductDto]

  def listAllByCategoryId(categoryId:Integer):Array[ProductDto]

  def deleteProductsByCategoryId(categoryId:Integer):Unit


}
