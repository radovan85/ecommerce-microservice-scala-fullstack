package com.radovan.spring.controllers

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.web.bind.annotation._
import org.springframework.web.context.request.{RequestAttributes, RequestContextHolder}

import com.fasterxml.jackson.databind.JsonNode
import com.radovan.spring.services.ProductCategoryService

@RestController
@RequestMapping(value=Array("/api/categories"))
class GatewayProductCategoryController {

  @Autowired
  private var categoryService: ProductCategoryService = _

  private def setAuthorizationHeader(authorizationHeader: String): Unit = {
    RequestContextHolder.getRequestAttributes.setAttribute("Authorization", authorizationHeader, RequestAttributes.SCOPE_REQUEST)
  }

  @PostMapping(value=Array("/addCategory"))
  def createCategory(
                      @RequestHeader(value = "Authorization", required = false) authorizationHeader: String,
                      @RequestBody category: JsonNode): ResponseEntity[String] = {
    setAuthorizationHeader(authorizationHeader)
    new ResponseEntity(categoryService.addCategory(category), HttpStatus.OK)
  }

  @GetMapping(value=Array("/categoryDetails/{categoryId}"))
  def getCategoryDetails(
                          @RequestHeader(value = "Authorization", required = false) authorizationHeader: String,
                          @PathVariable("categoryId") categoryId: Integer): ResponseEntity[JsonNode] = {
    setAuthorizationHeader(authorizationHeader)
    new ResponseEntity(categoryService.getCategoryById(categoryId), HttpStatus.OK)
  }

  @PutMapping(value=Array("/updateCategory/{categoryId}"))
  def updateCategory(
                      @RequestHeader(value = "Authorization", required = false) authorizationHeader: String,
                      @PathVariable("categoryId") categoryId: Integer,
                      @RequestBody category: JsonNode): ResponseEntity[String] = {
    setAuthorizationHeader(authorizationHeader)
    new ResponseEntity(categoryService.updateCategory(category, categoryId), HttpStatus.OK)
  }

  @DeleteMapping(value=Array("/deleteCategory/{categoryId}"))
  def deleteCategory(
                      @RequestHeader(value = "Authorization", required = false) authorizationHeader: String,
                      @PathVariable("categoryId") categoryId: Integer): ResponseEntity[String] = {
    setAuthorizationHeader(authorizationHeader)
    new ResponseEntity(categoryService.deleteCategory(categoryId), HttpStatus.OK)
  }

  @GetMapping(value=Array("/allCategories"))
  def getAllCategories(
                        @RequestHeader(value = "Authorization", required = false) authorizationHeader: String): ResponseEntity[Array[JsonNode]] = {
    setAuthorizationHeader(authorizationHeader)
    new ResponseEntity(categoryService.listAll, HttpStatus.OK)
  }
}

