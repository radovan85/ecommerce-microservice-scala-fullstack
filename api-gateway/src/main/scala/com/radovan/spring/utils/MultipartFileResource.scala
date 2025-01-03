package com.radovan.spring.utils

import org.springframework.core.io.ByteArrayResource
import org.springframework.web.multipart.MultipartFile

class MultipartFileResource(file: MultipartFile) extends ByteArrayResource(file.getBytes) {
  val filename: String = file.getOriginalFilename

  override def getFilename: String = filename
}
