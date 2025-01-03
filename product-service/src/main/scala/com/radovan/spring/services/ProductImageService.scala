package com.radovan.spring.services

import com.radovan.spring.dto.ProductImageDto
import org.springframework.web.multipart.MultipartFile

trait ProductImageService {

  def addImage(file:MultipartFile,productId:Integer):ProductImageDto

  def deleteImage(imageId:Integer):Unit

  def listAll:Array[ProductImageDto]
}
