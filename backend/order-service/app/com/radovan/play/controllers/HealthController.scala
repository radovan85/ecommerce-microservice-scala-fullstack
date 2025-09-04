package com.radovan.play.controllers

import com.radovan.play.utils.ResponsePackage
import jakarta.inject.Inject
import org.apache.hc.core5.http.HttpStatus
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}

class HealthController @Inject()(
                                cc:ControllerComponents
                                ) extends AbstractController(cc){

  def healthCheck:Action[AnyContent] = Action {
    new ResponsePackage[String]("OK",HttpStatus.SC_OK).toResult
  }

}
