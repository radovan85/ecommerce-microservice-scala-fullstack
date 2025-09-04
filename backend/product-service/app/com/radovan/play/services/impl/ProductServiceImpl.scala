package com.radovan.play.services.impl

import com.radovan.play.brokers.ProductNatsSender
import com.radovan.play.converter.TempConverter
import com.radovan.play.dto.ProductDto
import com.radovan.play.exceptions.InstanceUndefinedException
import com.radovan.play.repositories.ProductRepository
import com.radovan.play.services.{ProductCategoryService, ProductService}
import jakarta.inject.{Inject, Singleton}

@Singleton
class ProductServiceImpl extends ProductService{

  private var productRepository:ProductRepository = _
  private var categoryService:ProductCategoryService = _
  private var natsSender:ProductNatsSender = _
  private var tempConverter:TempConverter = _

  @Inject
  private def initialize(productRepository: ProductRepository,categoryService: ProductCategoryService,
                         natsSender: ProductNatsSender,tempConverter: TempConverter):Unit = {
    this.productRepository = productRepository
    this.categoryService = categoryService
    this.natsSender = natsSender
    this.tempConverter = tempConverter
  }

  override def addProduct(productDto: ProductDto): ProductDto = {
    val storedProduct = productRepository.save(tempConverter.productDtoToEntity(productDto))
    tempConverter.productEntityToDto(storedProduct)
  }

  override def getProductById(productId: Int): ProductDto = {
    productRepository.findById(productId) match {
      case Some(productEntity) => tempConverter.productEntityToDto(productEntity)
      case None => throw new InstanceUndefinedException("The product has not been found")
    }
  }

  override def updateProduct(productDto: ProductDto, productId: Int,jwtToken:String): ProductDto = {
    categoryService.getCategoryById(productDto.getProductCategoryId())
    val currentProduct = getProductById(productId)
    productDto.setProductId(currentProduct.getProductId())
    if (productDto.getImageId() == null && currentProduct.getImageId() != null) {
      productDto.setImageId(currentProduct.getImageId())
    }
    val updatedProduct = productRepository.save(tempConverter.productDtoToEntity(productDto))
    natsSender.sendCartUpdateRequest(productId, jwtToken )
    tempConverter.productEntityToDto(updatedProduct)
  }

  override def deleteProduct(productId: Int,jwtToken:String): Unit = {
    getProductById(productId)
    natsSender.sendCartDeleteRequest(productId,jwtToken)
    productRepository.deleteById(productId)
  }

  override def listAll: Array[ProductDto] = {
    productRepository.findAll.collect{
      case productEntity => tempConverter.productEntityToDto(productEntity)
    }
  }

  override def listAllByCategoryId(categoryId: Int): Array[ProductDto] = {
    productRepository.findAllByCategoryId(categoryId).collect{
      case productEntity => tempConverter.productEntityToDto(productEntity)
    }
  }

  override def deleteProductsByCategoryId(categoryId: Int,jwtToken:String): Unit = {
    listAllByCategoryId(categoryId).foreach(product => deleteProduct(product.getProductId(),jwtToken))
  }
}
