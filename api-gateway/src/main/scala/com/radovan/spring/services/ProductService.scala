package com.radovan.spring.services

import com.fasterxml.jackson.databind.JsonNode

trait ProductService {

  def createProduct(product:JsonNode):String

  def getProductById(productId:Integer):JsonNode

  def updateProduct(product:JsonNode, productId:Integer):String

  def deleteProduct(productId:Integer):String

  def listAll:Array[JsonNode]

  def listAllByCategoryId(categoryId:Integer):Array[JsonNode]
}
