package com.radovan.spring.services

import com.fasterxml.jackson.databind.JsonNode

trait ProductCategoryService {

  def addCategory(category: JsonNode): String

  def getCategoryById(categoryId: Integer): JsonNode

  def updateCategory(category: JsonNode, categoryId: Integer): String

  def deleteCategory(categoryId: Integer): String

  def listAll: Array[JsonNode]
}
