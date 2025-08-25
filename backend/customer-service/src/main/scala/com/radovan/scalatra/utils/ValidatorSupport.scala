package com.radovan.scalatra.utils

import com.radovan.scalatra.exceptions.DataNotValidatedException
import jakarta.validation.{ Validation, Validator}

trait ValidatorSupport {

  private lazy val validator: Validator =
    Validation.buildDefaultValidatorFactory().getValidator

  def validateOrHalt[A](obj: A): Unit = {
    val violations = validator.validate(obj)

    if (!violations.isEmpty) {
      throw new DataNotValidatedException("The form has not been validated")
    }
  }
}