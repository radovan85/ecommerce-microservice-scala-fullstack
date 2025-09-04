package com.radovan.play.controllers

import com.radovan.play.utils.TokenUtils._
import com.radovan.play.dto.ProductDto
import com.radovan.play.security.{JwtSecuredAction, SecuredRequest}
import com.radovan.play.services.{ProductImageService, ProductService}
import com.radovan.play.utils.{ResponsePackage, ValidatorSupport}
import flexjson.JSONDeserializer
import jakarta.inject.Inject
import org.apache.hc.core5.http.HttpStatus
import play.api.libs.Files.TemporaryFile
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents, MultipartFormData, Result}

class ProductController @Inject()(
                                   cc: ControllerComponents,
                                   imageService: ProductImageService,
                                   productService: ProductService,
                                   securedAction: JwtSecuredAction
                                 ) extends AbstractController(cc) with ValidatorSupport{


  private def onlyAdmin[A](secured: SecuredRequest[A])(block: => Result): Result = {
    if (secured.roles.contains("ROLE_ADMIN")) block
    else Forbidden("Access denied: admin role required")
  }

  def listAllProducts: Action[AnyContent] = securedAction {
    new ResponsePackage(productService.listAll, HttpStatus.SC_OK).toResult
  }

  def getProductDetails(productId:Int):Action[AnyContent] = Action{
    new ResponsePackage(productService.getProductById(productId), HttpStatus.SC_OK).toResult
  }

  def createProduct:Action[AnyContent] = securedAction { secured =>
    onlyAdmin(secured){
      val json = Json.stringify(secured.body.asJson.getOrElse(Json.obj()))
      val product = new JSONDeserializer[ProductDto]()
        .use(null, classOf[ProductDto])
        .deserialize(json, classOf[ProductDto])

      validateOrHalt(product)
      val storedProduct = productService.addProduct(product)
      new ResponsePackage(s"Product with id ${storedProduct.getProductId()} has been stored!",HttpStatus.SC_CREATED).toResult
    }
  }

  def updateProduct(productId:Int):Action[AnyContent] = securedAction { secured =>
    onlyAdmin(secured){
      val json = Json.stringify(secured.body.asJson.getOrElse(Json.obj()))
      val product = new JSONDeserializer[ProductDto]()
        .use(null, classOf[ProductDto])
        .deserialize(json, classOf[ProductDto])

      validateOrHalt(product)
      val updatedProduct = productService.updateProduct(product, productId,provideToken(secured))
      new ResponsePackage(s"Product with id ${updatedProduct.getProductId()} has been updated without any issues!",HttpStatus.SC_OK).toResult
    }
  }

  def deleteProduct(productId:Int):Action[AnyContent] = securedAction { secured =>
    onlyAdmin(secured){
      productService.deleteProduct(productId,provideToken(secured))
      new ResponsePackage(s"Product with id ${productId} has been permanently deleted!",HttpStatus.SC_OK).toResult
    }
  }

  def getAllImages:Action[AnyContent] = securedAction {
      new ResponsePackage(imageService.listAll,HttpStatus.SC_OK).toResult
  }

  def uploadProductImage(productId: Int): Action[MultipartFormData[TemporaryFile]] = securedAction(parse.multipartFormData) { secured =>
    onlyAdmin(secured) {
      val maybeFile = secured.body.file("file")
      maybeFile match {
        case Some(file) =>
          val storedImage = imageService.addImage(file, productId)
          new ResponsePackage(s"✅ Image '${storedImage.getName()}' uploaded for product $productId", HttpStatus.SC_CREATED).toResult
        case None =>
          new ResponsePackage("❌ No image file provided in request", HttpStatus.SC_BAD_REQUEST).toResult
      }
    }
  }
}
