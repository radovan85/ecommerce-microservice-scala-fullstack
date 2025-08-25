package com.radovan.play.services

import com.radovan.play.dto.ProductImageDto
import play.api.libs.Files.TemporaryFile
import play.api.mvc.MultipartFormData.FilePart

trait ProductImageService {

  def deleteImage(imageId:Int):Unit

  def addImage(file: FilePart[TemporaryFile], productId: Integer): ProductImageDto

  def listAll:Array[ProductImageDto]
}
