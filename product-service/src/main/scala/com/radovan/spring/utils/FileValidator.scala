package com.radovan.spring.utils

import org.apache.commons.io.FilenameUtils
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile

import com.radovan.spring.exceptions.DataNotValidatedException

@Component
class FileValidator {

  def validateFile(file: MultipartFile): Boolean = {
    val extension = FilenameUtils.getExtension(file.getOriginalFilename)
    if (isSupportedExtension(extension)) {
      true
    } else {
      val error = new Error("The file is not valid!")
      throw new DataNotValidatedException(error)
    }
  }

  private def isSupportedExtension(extension: String): Boolean = {
    val extensionOption = Option(extension)
    extensionOption.exists(ext => ext == "png" || ext == "jpeg" || ext == "jpg")
  }
}