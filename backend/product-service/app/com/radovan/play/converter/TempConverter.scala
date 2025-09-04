package com.radovan.play.converter

import com.radovan.play.dto.{ProductCategoryDto, ProductDto, ProductImageDto}
import com.radovan.play.entity.{ProductCategoryEntity, ProductEntity, ProductImageEntity}
import com.radovan.play.repositories.{ProductCategoryRepository, ProductImageRepository, ProductRepository}
import jakarta.inject.{Inject, Singleton}
import org.modelmapper.ModelMapper

import java.text.DecimalFormat

@Singleton
class TempConverter {

  private val decfor = new DecimalFormat("0.00")
  private var mapper:ModelMapper = _
  private var imageRepository:ProductImageRepository = _
  private var categoryRepository:ProductCategoryRepository = _
  private var productRepository:ProductRepository = _

  @Inject
  private def initialize(mapper: ModelMapper,imageRepository: ProductImageRepository,
                         categoryRepository: ProductCategoryRepository,productRepository: ProductRepository):Unit = {
    this.mapper = mapper
    this.imageRepository = imageRepository
    this.categoryRepository = categoryRepository
    this.productRepository = productRepository
  }

  def imageEntityToDto(imageEntity: ProductImageEntity):ProductImageDto = {
    val returnValue = mapper.map(imageEntity, classOf[ProductImageDto])
    val productOption = Option(imageEntity.getProduct())
    productOption match {
      case Some(productEntity) => returnValue.setProductId(productEntity.getProductId())
      case None =>
    }

    returnValue
  }

  def imageDtoToEntity(imageDto: ProductImageDto):ProductImageEntity = {
    val returnValue = mapper.map(imageDto,classOf[ProductImageEntity])
    val productIdOption = Option(imageDto.getProductId())
    productIdOption match {
      case Some(productId) =>
        productRepository.findById(productId) match {
          case Some(productEntity) => returnValue.setProduct(productEntity)
          case None =>
        }
      case None =>
    }

    returnValue
  }

  def categoryEntityToDto(categoryEntity: ProductCategoryEntity):ProductCategoryDto = {
    mapper.map(categoryEntity, classOf[ProductCategoryDto])
  }

  def categoryDtoToEntity(categoryDto: ProductCategoryDto):ProductCategoryEntity = {
    mapper.map(categoryDto, classOf[ProductCategoryEntity])
  }

  def productEntityToDto(productEntity:ProductEntity):ProductDto = {
    val returnValue = mapper.map(productEntity, classOf[ProductDto])
    returnValue.setProductPrice(decfor.format(returnValue.getProductPrice()).toFloat)
    returnValue.setDiscount(decfor.format(returnValue.getDiscount()).toFloat)
    val categoryOption = Option(productEntity.getProductCategory())
    categoryOption match {
      case Some(categoryEntity) => returnValue.setProductCategoryId(categoryEntity.getProductCategoryId())
      case None =>
    }

    val imageOption = Option(productEntity.getImage())
    imageOption match {
      case Some(imageEntity) => returnValue.setImageId(imageEntity.getId())
      case None =>
    }

    returnValue
  }

  def productDtoToEntity(productDto: ProductDto):ProductEntity = {
    val returnValue = mapper.map(productDto, classOf[ProductEntity])
    returnValue.setProductPrice(decfor.format(returnValue.getProductPrice()).toFloat)
    returnValue.setDiscount(decfor.format(returnValue.getDiscount()).toFloat)
    val categoryIdOption = Option(productDto.getProductCategoryId())
    categoryIdOption match {
      case Some(categoryId) =>
        categoryRepository.findById(categoryId) match {
          case Some(categoryEntity) => returnValue.setProductCategory(categoryEntity)
          case None =>
        }
      case None =>
    }

    val imageIdOption = Option(productDto.getImageId())
    imageIdOption match {
      case Some(imageId) =>
        imageRepository.findById(imageId) match {
          case Some(imageEntity) => returnValue.setImage(imageEntity)
          case None =>
        }
      case None =>
    }

    returnValue
  }
}
