package com.radovan.spring.controllers

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.web.bind.annotation._
import org.springframework.web.context.request.{RequestAttributes, RequestContextHolder}
import org.springframework.web.multipart.MultipartFile

import com.fasterxml.jackson.databind.JsonNode
import com.radovan.spring.services.{ProductImageService, ProductService}

@RestController
@RequestMapping(value = Array("/api/products"))
class GatewayProductController {

  private var productService: ProductService = _
  private var imageService: ProductImageService = _

  @Autowired
  private def initialize(productService: ProductService, imageService: ProductImageService): Unit = {
    this.productService = productService
    this.imageService = imageService
  }

  private def setAuthorizationHeader(authorizationHeader: String): Unit = {
    RequestContextHolder.getRequestAttributes.setAttribute("Authorization", authorizationHeader, RequestAttributes.SCOPE_REQUEST)
  }

  @PostMapping(value = Array("/createProduct"))
  def createProduct(
                     @RequestHeader(value = "Authorization", required = false) authorizationHeader: String,
                     @RequestBody product: JsonNode): ResponseEntity[String] = {
    setAuthorizationHeader(authorizationHeader)
    new ResponseEntity(productService.createProduct(product), HttpStatus.OK)
  }

  @GetMapping(value = Array("/productDetails/{productId}"))
  def getProductDetails(
                         @RequestHeader(value = "Authorization", required = false) authorizationHeader: String,
                         @PathVariable("productId") productId: Integer): ResponseEntity[JsonNode] = {
    setAuthorizationHeader(authorizationHeader)
    new ResponseEntity(productService.getProductById(productId), HttpStatus.OK)
  }

  @PutMapping(value = Array("/updateProduct/{productId}"))
  def updateProduct(
                     @RequestHeader(value = "Authorization", required = false) authorizationHeader: String,
                     @RequestBody product: JsonNode,
                     @PathVariable("productId") productId: Integer): ResponseEntity[String] = {
    setAuthorizationHeader(authorizationHeader)
    new ResponseEntity(productService.updateProduct(product, productId), HttpStatus.OK)
  }

  @DeleteMapping(value = Array("/deleteProduct/{productId}"))
  def deleteProduct(
                     @RequestHeader(value = "Authorization", required = false) authorizationHeader: String,
                     @PathVariable("productId") productId: Integer): ResponseEntity[String] = {
    setAuthorizationHeader(authorizationHeader)
    new ResponseEntity(productService.deleteProduct(productId), HttpStatus.OK)
  }

  @GetMapping(value = Array("/allProducts"))
  def getAllProducts(
                      @RequestHeader(value = "Authorization", required = false) authorizationHeader: String): ResponseEntity[Array[JsonNode]] = {
    setAuthorizationHeader(authorizationHeader)
    new ResponseEntity(productService.listAll, HttpStatus.OK)
  }

  @GetMapping(value = Array("/allProducts/{categoryId}"))
  def getAllProductsByCategory(
                                @RequestHeader(value = "Authorization", required = false) authorizationHeader: String,
                                @PathVariable("categoryId") categoryId: Integer): ResponseEntity[Array[JsonNode]] = {
    setAuthorizationHeader(authorizationHeader)
    new ResponseEntity(productService.listAllByCategoryId(categoryId), HttpStatus.OK)
  }

  @PostMapping(value = Array("/storeImage/{productId}"))
  def storeImage(
                  @RequestHeader(value = "Authorization", required = false) authorizationHeader: String,
                  @RequestPart("file") file: MultipartFile,
                  @PathVariable("productId") productId: Integer): ResponseEntity[String] = {
    setAuthorizationHeader(authorizationHeader)
    new ResponseEntity(imageService.addImage(file, productId), HttpStatus.OK)
  }

  @GetMapping(value = Array("/allImages"))
  def getAllImages(
                    @RequestHeader(value = "Authorization", required = false) authorizationHeader: String): ResponseEntity[Array[JsonNode]] = {
    setAuthorizationHeader(authorizationHeader)
    new ResponseEntity(imageService.listAll, HttpStatus.OK)
  }
}
