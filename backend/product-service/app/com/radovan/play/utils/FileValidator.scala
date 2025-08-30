package com.radovan.play.utils

import com.radovan.play.exceptions.DataNotValidatedException
import jakarta.inject.Singleton
import org.apache.commons.io.FilenameUtils
import play.api.mvc.MultipartFormData.FilePart
import play.api.libs.Files.TemporaryFile

@Singleton
class FileValidator {

  def validateFile(file: FilePart[TemporaryFile]): Boolean = {
    val extensionOption = Option(FilenameUtils.getExtension(file.filename))
    extensionOption match {
      case Some(extension) if isSupportedExtension(extension) => true
      case _ => throw new DataNotValidatedException("The file is not valid!")
    }
  }

  private def isSupportedExtension(extension: String): Boolean = {
    extension == "png" || extension == "jpeg" || extension == "jpg"
  }
}
