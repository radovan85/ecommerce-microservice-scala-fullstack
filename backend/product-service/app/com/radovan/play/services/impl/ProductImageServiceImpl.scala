package com.radovan.play.services.impl

import com.radovan.play.converter.TempConverter
import com.radovan.play.dto.ProductImageDto
import com.radovan.play.exceptions.FileUploadException
import com.radovan.play.repositories.ProductImageRepository
import com.radovan.play.services.{ProductImageService, ProductService}
import com.radovan.play.utils.FileValidator
import jakarta.inject.{Inject, Singleton}
import play.api.libs.Files.TemporaryFile
import play.api.mvc.MultipartFormData.FilePart

import java.nio.file.Files.readAllBytes
import scala.util.{Failure, Success, Try}

@Singleton
class ProductImageServiceImpl extends ProductImageService {

  private var imageRepository: ProductImageRepository = _
  private var productService: ProductService = _
  private var tempConverter: TempConverter = _
  private var fileValidator: FileValidator = _

  @Inject
  private def initialize(imageRepository: ProductImageRepository, productService: ProductService,
                         tempConverter: TempConverter, fileValidator: FileValidator): Unit = {
    this.imageRepository = imageRepository
    this.productService = productService
    this.tempConverter = tempConverter
    this.fileValidator = fileValidator
  }

  override def deleteImage(imageId: Int): Unit = {
    imageRepository.deleteById(imageId)
  }

  override def addImage(file: FilePart[TemporaryFile], productId: Integer): ProductImageDto = {
    // Validacija proizvoda i fajla
    productService.getProductById(productId)
    fileValidator.validateFile(file)

    // Briši postojeću sliku ako postoji
    imageRepository.findByProductId(productId).foreach(image => deleteImage(image.getId()))

    // Pokušaj da sačuvaš novu sliku
    Try {
      val imageDto = new ProductImageDto()
      imageDto.setProductId(productId)
      imageDto.setName(Option(file.filename).getOrElse(throw new FileUploadException("Filename is missing")))
      imageDto.setContentType(Option(file.contentType.orNull).getOrElse("application/octet-stream"))
      imageDto.setSize(file.ref.path.toFile.length())
      imageDto.setData(readAllBytes(file.ref.path))

      val storedImage = imageRepository.save(tempConverter.imageDtoToEntity(imageDto))
      tempConverter.imageEntityToDto(storedImage) // 👈 Evaluacija, ovo se vraća iz Try
    } match {
      case Success(dto) => dto
      case Failure(ex)  => throw new FileUploadException(ex.getMessage)
    }
  }


  override def listAll: Array[ProductImageDto] = {
    imageRepository.findAll.collect{
      case imageEntity => tempConverter.imageEntityToDto(imageEntity)
    }
  }


}
