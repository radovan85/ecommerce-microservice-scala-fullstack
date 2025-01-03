package com.radovan.spring.controllers

import com.fasterxml.jackson.databind.JsonNode
import com.radovan.spring.dto.{ProductDto, ProductImageDto}
import com.radovan.spring.exceptions.DataNotValidatedException
import com.radovan.spring.services.{ProductImageService, ProductService}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.Errors
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation._
import org.springframework.web.context.request.{RequestAttributes, RequestContextHolder}
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping(value = Array("/products"))
class ProductController {

  private var productService: ProductService = _
  private var imageService: ProductImageService = _

  @Autowired
  private def injectAll(productService: ProductService,imageService: ProductImageService):Unit = {
    this.productService = productService
    this.imageService = imageService
  }

  private def setAuthorizationHeader(authorizationHeader: String): Unit = {
    Option(RequestContextHolder.getRequestAttributes).foreach { attrs =>
      attrs.setAttribute("Authorization", authorizationHeader, RequestAttributes.SCOPE_REQUEST)
    }
  }

  @PreAuthorize(value = "hasAuthority('ADMIN')")
  @PostMapping(value = Array("/createProduct"))
  def createProduct(
                     @RequestHeader(value = "Authorization", required = false) authorizationHeader: String,
                     @RequestBody @Validated product: ProductDto,
                     errors: Errors
                   ): ResponseEntity[String] = {
    setAuthorizationHeader(authorizationHeader)
    if (errors.hasErrors) {
      throw new DataNotValidatedException(new Error("The product has not been validated!"))
    }
    val storedProduct = productService.addProduct(product)
    new ResponseEntity(s"The product with id ${storedProduct.getProductId} has been stored!",HttpStatus.OK)
  }

  @GetMapping(value = Array("/productDetails/{productId}"))
  def getProductDetails(
                         @RequestHeader(value = "Authorization", required = false) authorizationHeader: String,
                         @PathVariable("productId") productId: Integer
                       ): ResponseEntity[ProductDto] = {
    setAuthorizationHeader(authorizationHeader)
    new ResponseEntity(productService.getProductById(productId),HttpStatus.OK)
  }

  @PreAuthorize(value = "hasAuthority('ADMIN')")
  @PutMapping(value = Array("/updateProduct/{productId}"))
  def updateProduct(
                     @RequestHeader(value = "Authorization", required = false) authorizationHeader: String,
                     @RequestBody @Validated product: ProductDto,
                     @PathVariable("productId") productId: Integer,
                     errors: Errors
                   ): ResponseEntity[String] = {
    setAuthorizationHeader(authorizationHeader)
    if (errors.hasErrors) {
      throw new DataNotValidatedException(new Error("The product has not been validated!"))
    }
    val updatedProduct = productService.updateProduct(product, productId)
    new ResponseEntity(s"The product with id ${updatedProduct.getProductId} has been updated!",HttpStatus.OK)
  }

  @PreAuthorize(value = "hasAuthority('ROLE_USER')")
  @PutMapping(value = Array("/orderUpdateProduct/{productId}"))
  def updateProductFromOrderService(
                                     @RequestHeader(value = "Authorization", required = false) authorizationHeader: String,
                                     @RequestHeader(value = "X-Source-Service", required = false) sourceService: String,
                                     @RequestBody @Validated product: JsonNode,
                                     @PathVariable("productId") productId: Integer,
                                     errors: Errors
                                   ): ResponseEntity[Unit] = {
    if (!"order-service".equals(sourceService)) {
      new ResponseEntity(HttpStatus.FORBIDDEN)
    }
    setAuthorizationHeader(authorizationHeader)
    if (errors.hasErrors) {
      throw new DataNotValidatedException(new Error("The product has not been validated!"))
    }
    productService.updateProduct(product, productId)
    new ResponseEntity(HttpStatus.OK)
  }

  @PreAuthorize(value = "hasAuthority('ADMIN')")
  @DeleteMapping(value = Array("/deleteProduct/{productId}"))
  def deleteProduct(
                     @RequestHeader(value = "Authorization", required = false) authorizationHeader: String,
                     @PathVariable("productId") productId: Integer
                   ): ResponseEntity[String] = {
    setAuthorizationHeader(authorizationHeader)
    productService.deleteProduct(productId)
    new ResponseEntity(s"The product with id $productId has been permanently deleted!",HttpStatus.OK)
  }

  @GetMapping(value = Array("/allProducts"))
  def getAllProducts(
                      @RequestHeader(value = "Authorization", required = false) authorizationHeader: String
                    ): ResponseEntity[Array[ProductDto]] = {
    setAuthorizationHeader(authorizationHeader)
    new ResponseEntity(productService.listAll,HttpStatus.OK)
  }

  @GetMapping(value = Array("/allProducts/{categoryId}"))
  def getAllProductsByCategory(
                                @RequestHeader(value = "Authorization", required = false) authorizationHeader: String,
                                @PathVariable("categoryId") categoryId: Integer
                              ): ResponseEntity[Array[ProductDto]] = {
    setAuthorizationHeader(authorizationHeader)
    new ResponseEntity(productService.listAllByCategoryId(categoryId),HttpStatus.OK)
  }

  @PreAuthorize(value = "hasAuthority('ADMIN')")
  @DeleteMapping(value = Array("/deleteProducts/{categoryId}"))
  def deleteProductsByCategory(
                                @RequestHeader(value = "Authorization", required = false) authorizationHeader: String,
                                @PathVariable("categoryId") categoryId: Integer
                              ): ResponseEntity[Void] = {
    setAuthorizationHeader(authorizationHeader)
    productService.deleteProductsByCategoryId(categoryId)
    ResponseEntity.ok().build()
  }

  @PreAuthorize(value = "hasAuthority('ADMIN')")
  @PostMapping(value = Array("/storeImage/{productId}"))
  def storeImage(
                  @RequestPart("file") file: MultipartFile,
                  @PathVariable("productId") productId: Integer,
                  @RequestHeader(value = "Authorization", required = false) authorizationHeader: String
                ): ResponseEntity[String] = {
    setAuthorizationHeader(authorizationHeader)
    imageService.addImage(file, productId)
    ResponseEntity.ok("The image has been added without any issues!")
  }

  @GetMapping(value = Array("/allImages"))
  def getAllImages(
                    @RequestHeader(value = "Authorization", required = false) authorizationHeader: String
                  ): ResponseEntity[Array[ProductImageDto]] = {
    setAuthorizationHeader(authorizationHeader)
    new ResponseEntity(imageService.listAll,HttpStatus.OK)
  }
}

