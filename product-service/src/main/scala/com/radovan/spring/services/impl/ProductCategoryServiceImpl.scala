package com.radovan.spring.services.impl

import com.radovan.spring.converter.TempConverter
import com.radovan.spring.dto.ProductCategoryDto
import com.radovan.spring.exceptions.{ExistingInstanceException, InstanceUndefinedException}
import com.radovan.spring.repositories.ProductCategoryRepository
import com.radovan.spring.services.{ProductCategoryService, ProductService}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import scala.jdk.CollectionConverters._

@Service
class ProductCategoryServiceImpl extends ProductCategoryService {

  private var categoryRepository: ProductCategoryRepository = _
  private var tempConverter: TempConverter = _
  private var productService:ProductService = _

  @Autowired
  private def initialize(categoryRepository: ProductCategoryRepository, tempConverter: TempConverter,
                         productService: ProductService): Unit = {
    this.categoryRepository = categoryRepository
    this.tempConverter = tempConverter
    this.productService = productService
  }

  @Transactional
  override def addCategory(category: ProductCategoryDto): ProductCategoryDto = {
    val categoryOption = categoryRepository.findByName(category.getName)
    categoryOption match {
      case Some(_) => throw new ExistingInstanceException(new Error("This category already exists!"))
      case None =>
        val storedCategory = categoryRepository.save(tempConverter.categoryDtoToEntity(category))
        tempConverter.categoryEntityToDto(storedCategory)
    }
  }

  @Transactional(readOnly = true)
  override def getCategoryById(categoryId: Integer): ProductCategoryDto = {
    val categoryEntity = categoryRepository.findById(categoryId).orElseThrow(() => new InstanceUndefinedException(new Error("The category has not been found")))
    tempConverter.categoryEntityToDto(categoryEntity)
  }

  @Transactional
  override def updateCategory(category: ProductCategoryDto, categoryId: Integer): ProductCategoryDto = {
    val currentCategory = getCategoryById(categoryId)
    val categoryOption = categoryRepository.findByName(category.getName)
    categoryOption.filter(_.getProductCategoryId != currentCategory.getProductCategoryId).foreach { _ =>
      throw new ExistingInstanceException(new Error("This category already exists!"))
    }

    category.setProductCategoryId(currentCategory.getProductCategoryId)
    val updatedCategory = categoryRepository.saveAndFlush(tempConverter.categoryDtoToEntity(category))
    tempConverter.categoryEntityToDto(updatedCategory)
  }

  @Transactional
  override def deleteCategory(categoryId: Integer): Unit = {
    getCategoryById(categoryId)
    productService.deleteProductsByCategoryId(categoryId)
    categoryRepository.deleteById(categoryId)
    categoryRepository.flush()
  }

  @Transactional(readOnly = true)
  override def listAll: Array[ProductCategoryDto] = {
    val allCategories = categoryRepository.findAll().asScala
    allCategories.collect {
      case categoryEntity => tempConverter.categoryEntityToDto(categoryEntity)
    }.toArray
  }
}
