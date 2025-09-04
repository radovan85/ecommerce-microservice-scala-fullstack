package com.radovan.play.services.impl

import com.radovan.play.converter.TempConverter
import com.radovan.play.dto.ProductCategoryDto
import com.radovan.play.exceptions.{ExistingInstanceException, InstanceUndefinedException}
import com.radovan.play.repositories.ProductCategoryRepository
import com.radovan.play.services.{ProductCategoryService, ProductService}
import jakarta.inject.{Inject, Provider, Singleton}

@Singleton
class ProductCategoryServiceImpl extends ProductCategoryService{

  private var categoryRepository:ProductCategoryRepository = _
  private var productServiceProvider:Provider[ProductService] = _
  private var tempConverter:TempConverter = _

  @Inject
  private def initialize(categoryRepository: ProductCategoryRepository,productServiceProvider:Provider[ProductService],
                         tempConverter: TempConverter):Unit = {
    this.categoryRepository = categoryRepository
    this.productServiceProvider = productServiceProvider
    this.tempConverter = tempConverter
  }

  private def productService = productServiceProvider.get()

  override def addCategory(categoryDto: ProductCategoryDto): ProductCategoryDto = {
    categoryRepository.findByName(categoryDto.getName()) match {
      case Some(value) => throw new ExistingInstanceException("This category already exists!")
      case None =>
        val storedCategory = categoryRepository.save(tempConverter.categoryDtoToEntity(categoryDto))
        tempConverter.categoryEntityToDto(storedCategory)
    }
  }

  override def getCategoryById(categoryId: Int): ProductCategoryDto = {
    categoryRepository.findById(categoryId) match {
      case Some(categoryEntity) => tempConverter.categoryEntityToDto(categoryEntity)
      case None => throw new InstanceUndefinedException("The category has not been found!")
    }
  }

  override def updateCategory(categoryDto: ProductCategoryDto, categoryId: Int): ProductCategoryDto = {
    getCategoryById(categoryId)
    categoryRepository.findByName(categoryDto.getName()) match {
      case Some(categoryEntity) =>
        if(categoryEntity.getProductCategoryId() != categoryId) throw new ExistingInstanceException("This category already exists!")
      case None =>
    }

    categoryDto.setProductCategoryId(categoryId)
    val updatedCategory = categoryRepository.save(tempConverter.categoryDtoToEntity(categoryDto))
    tempConverter.categoryEntityToDto(updatedCategory)

  }

  override def deleteCategory(categoryId: Int,jwtToken:String): Unit = {
    getCategoryById(categoryId)
    productService.deleteProductsByCategoryId(categoryId,jwtToken)
    categoryRepository.deleteById(categoryId)
  }

  override def listAll: Array[ProductCategoryDto] = {
    categoryRepository.findAll.collect{
      case categoryEntity => tempConverter.categoryEntityToDto(categoryEntity)
    }
  }
}
