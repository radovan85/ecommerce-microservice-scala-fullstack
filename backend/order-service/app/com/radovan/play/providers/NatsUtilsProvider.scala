package com.radovan.play.providers

import com.google.inject.Provider
import com.radovan.play.utils.NatsUtils
import jakarta.inject.Inject
import play.api.inject.ApplicationLifecycle

import scala.concurrent.Future

class NatsUtilsProvider @Inject()(lifecycle: ApplicationLifecycle) extends Provider[NatsUtils] {

  private val natsUtils = new NatsUtils
  natsUtils.init()


  lifecycle.addStopHook { () =>
    Future.successful {
      natsUtils.closeConnection()
    }
  }

  override def get(): NatsUtils = natsUtils
}