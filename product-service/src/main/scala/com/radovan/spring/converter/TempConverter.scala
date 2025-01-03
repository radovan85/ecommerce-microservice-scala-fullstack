package com.radovan.spring.converter

import com.radovan.spring.dto.{ProductCategoryDto, ProductDto, ProductImageDto}
import com.radovan.spring.entity.{ProductCategoryEntity, ProductEntity, ProductImageEntity}
import com.radovan.spring.repositories.{ProductCategoryRepository, ProductImageRepository, ProductRepository}
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.text.DecimalFormat

@Component
class TempConverter {

  private val decfor = new DecimalFormat("0.00")
  private var mapper:ModelMapper = _
  private var imageRepository:ProductImageRepository = _
  private var categoryRepository:ProductCategoryRepository = _
  private var productRepository:ProductRepository = _

  @Autowired
  private def initialize(mapper:ModelMapper,imageRepository: ProductImageRepository,
                         categoryRepository: ProductCategoryRepository,productRepository: ProductRepository):Unit = {
    this.mapper = mapper
    this.imageRepository = imageRepository
    this.categoryRepository = categoryRepository
    this.productRepository = productRepository
  }

  def productEntityToDto(product: ProductEntity): ProductDto = {
    val returnValue = mapper.map(product, classOf[ProductDto])
    returnValue.setProductPrice(decfor.format(returnValue.getProductPrice).toFloat)
    returnValue.setDiscount(decfor.format(returnValue.getDiscount).toFloat)
    val imageOption = Option(product.getImage)
    if (imageOption.isDefined) {
      returnValue.setImageId(imageOption.get.getId)
    }

    val categoryOption = Option(product.getProductCategory)
    if (categoryOption.isDefined) {
      returnValue.setProductCategoryId(categoryOption.get.getProductCategoryId)
    }

    returnValue
  }

  def productDtoToEntity(product: ProductDto): ProductEntity = {
    val returnValue = mapper.map(product, classOf[ProductEntity])
    returnValue.setProductPrice(decfor.format(returnValue.getProductPrice).toFloat)
    returnValue.setDiscount(decfor.format(returnValue.getDiscount).toFloat)
    val imageIdOption = Option(product.getImageId)
    imageIdOption match {
      case Some(imageId) =>
        val imageEntity = imageRepository.findById(imageId).orElse(null)
        if (imageEntity != null) {
          returnValue.setImage(imageEntity)
        }
      case None =>
    }

    val categoryIdOption = Option(product.getProductCategoryId)
    categoryIdOption match {
      case Some(categoryId) =>
        val categoryEntity = categoryRepository.findById(categoryId).orElse(null)
        if (categoryEntity != null) {
          returnValue.setProductCategory(categoryEntity)
        }
      case None =>
    }

    returnValue
  }


  def productImageEntityToDto(image: ProductImageEntity): ProductImageDto = {
    val returnValue = mapper.map(image, classOf[ProductImageDto])
    val productOption = Option(image.getProduct)
    if (productOption.isDefined) returnValue.setProductId(productOption.get.getProductId)
    returnValue
  }

   def productImageDtoToEntity(image: ProductImageDto): ProductImageEntity = {
    val returnValue = mapper.map(image, classOf[ProductImageEntity])
    val productIdOption = Option(image.getProductId)
    productIdOption match {
      case Some(productId) =>
        val productEntity = productRepository.findById(productId).orElse(null)
        if (productEntity != null) returnValue.setProduct(productEntity)
      case None =>
    }
    returnValue
  }

  def categoryEntityToDto(category: ProductCategoryEntity): ProductCategoryDto = mapper.map(category, classOf[ProductCategoryDto])

  def categoryDtoToEntity(category: ProductCategoryDto): ProductCategoryEntity = mapper.map(category, classOf[ProductCategoryEntity])


}
