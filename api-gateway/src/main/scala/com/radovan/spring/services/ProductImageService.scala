package com.radovan.spring.services

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.web.multipart.MultipartFile

trait ProductImageService {

  def addImage(file:MultipartFile, productId:Integer):String

  def listAll:Array[JsonNode]
}
