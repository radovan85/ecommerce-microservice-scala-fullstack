package com.radovan.play.controllers

import com.radovan.play.utils.TokenUtils._
import com.radovan.play.dto.ProductCategoryDto
import com.radovan.play.security.{JwtSecuredAction, SecuredRequest}
import com.radovan.play.services.ProductCategoryService
import com.radovan.play.utils.{ResponsePackage, ValidatorSupport}
import flexjson.JSONDeserializer
import jakarta.inject.Inject
import org.apache.hc.core5.http.HttpStatus
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents, Result}

class ProductCategoryController @Inject()(
                                           cc: ControllerComponents,
                                           categoryService: ProductCategoryService,
                                           securedAction: JwtSecuredAction
                                         ) extends AbstractController(cc) with ValidatorSupport {

  private def onlyAdmin[A](secured: SecuredRequest[A])(block: => Result): Result = {
    if (secured.roles.contains("ROLE_ADMIN")) block
    else Forbidden("Access denied: admin role required")
  }

  def listAll: Action[AnyContent] = securedAction {
    new ResponsePackage(categoryService.listAll, HttpStatus.SC_OK).toResult
  }

  def getCategoryDetails(categoryId: Int): Action[AnyContent] = securedAction {
    new ResponsePackage(categoryService.getCategoryById(categoryId), HttpStatus.SC_OK).toResult
  }

  def addCategory(): Action[AnyContent] = securedAction { secured =>
    onlyAdmin(secured) {
      val json = Json.stringify(secured.body.asJson.getOrElse(Json.obj()))
      val category = new JSONDeserializer[ProductCategoryDto]()
        .use(null, classOf[ProductCategoryDto])
        .deserialize(json, classOf[ProductCategoryDto])

      validateOrHalt(category)
      val storedCategory = categoryService.addCategory(category)
      new ResponsePackage(
        s"Category with id ${storedCategory.getProductCategoryId()} has been created!",
        HttpStatus.SC_CREATED
      ).toResult
    }
  }

  def updateCategory(categoryId: Int): Action[AnyContent] = securedAction { secured =>
    onlyAdmin(secured) {
      val json = Json.stringify(secured.body.asJson.getOrElse(Json.obj()))
      val category = new JSONDeserializer[ProductCategoryDto]()
        .use(null, classOf[ProductCategoryDto])
        .deserialize(json, classOf[ProductCategoryDto])

      validateOrHalt(category)
      val updatedCategory = categoryService.updateCategory(category, categoryId)
      new ResponsePackage(
        s"Category with id ${updatedCategory.getProductCategoryId()} has been updated without any issues!",
        HttpStatus.SC_OK
      ).toResult
    }
  }

  def deleteCategory(categoryId: Int): Action[AnyContent] = securedAction { secured =>
    onlyAdmin(secured) {
      categoryService.deleteCategory(categoryId,provideToken(secured))
      new ResponsePackage(
        s"Category with id $categoryId has been permanently deleted!",
        HttpStatus.SC_OK
      ).toResult
    }
  }
}
