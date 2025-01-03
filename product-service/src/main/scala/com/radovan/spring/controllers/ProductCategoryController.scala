package com.radovan.spring.controllers

import com.radovan.spring.dto.ProductCategoryDto
import com.radovan.spring.exceptions.DataNotValidatedException
import com.radovan.spring.services.ProductCategoryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.Errors
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation._
import org.springframework.web.context.request.{RequestAttributes, RequestContextHolder}

@RestController
@RequestMapping(value = Array("/categories"))
class ProductCategoryController {

  @Autowired
  private var categoryService: ProductCategoryService = _

  private def setAuthorizationHeader(authorizationHeader: String): Unit = {
    RequestContextHolder.getRequestAttributes.setAttribute("Authorization", authorizationHeader, RequestAttributes.SCOPE_REQUEST)
  }

  @PreAuthorize(value = "hasAuthority('ADMIN')")
  @PostMapping(value = Array("/addCategory"))
  def addCategory(
                   @RequestHeader(value = "Authorization", required = false) authorizationHeader: String,
                   @RequestBody @Validated category: ProductCategoryDto,
                   errors: Errors
                 ): ResponseEntity[String] = {
    setAuthorizationHeader(authorizationHeader)
    if (errors.hasErrors) {
      throw new DataNotValidatedException(new Error("The category has not been validated!"))
    }

    val storedCategory = categoryService.addCategory(category)
    new ResponseEntity(s"The category with id ${storedCategory.getProductCategoryId} has been stored!", HttpStatus.OK)
  }

  @GetMapping(value = Array("/categoryDetails/{categoryId}"))
  def getCategoryDetails(
                          @RequestHeader(value = "Authorization", required = false) authorizationHeader: String,
                          @PathVariable("categoryId") categoryId: Integer
                        ): ResponseEntity[ProductCategoryDto] = {
    setAuthorizationHeader(authorizationHeader)
    new ResponseEntity(categoryService.getCategoryById(categoryId), HttpStatus.OK)
  }

  @PreAuthorize(value = "hasAuthority('ADMIN')")
  @PutMapping(value = Array("/updateCategory/{categoryId}"))
  def updateCategory(
                      @RequestHeader(value = "Authorization", required = false) authorizationHeader: String,
                      @RequestBody @Validated category: ProductCategoryDto,
                      errors: Errors,
                      @PathVariable("categoryId") categoryId: Integer
                    ): ResponseEntity[String] = {
    setAuthorizationHeader(authorizationHeader)
    if (errors.hasErrors) {
      throw new DataNotValidatedException(new Error("The category has not been validated!"))
    }

    val updatedCategory = categoryService.updateCategory(category, categoryId)
    new ResponseEntity(s"The category with id ${updatedCategory.getProductCategoryId} has been updated without any issues!", HttpStatus.OK)
  }

  @PreAuthorize(value = "hasAuthority('ADMIN')")
  @DeleteMapping(value = Array("/deleteCategory/{categoryId}"))
  def deleteCategory(
                      @RequestHeader(value = "Authorization", required = false) authorizationHeader: String,
                      @PathVariable("categoryId") categoryId: Integer
                    ): ResponseEntity[String] = {
    setAuthorizationHeader(authorizationHeader)
    categoryService.deleteCategory(categoryId)
    new ResponseEntity(s"The category with id $categoryId has been permanently deleted!", HttpStatus.OK)
  }

  @GetMapping(value = Array("/allCategories"))
  def getAllCategories(
                        @RequestHeader(value = "Authorization", required = false) authorizationHeader: String
                      ): ResponseEntity[Array[ProductCategoryDto]] = {
    setAuthorizationHeader(authorizationHeader)
    new ResponseEntity(categoryService.listAll, HttpStatus.OK)
  }
}

