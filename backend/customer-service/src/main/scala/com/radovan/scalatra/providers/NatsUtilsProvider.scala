package com.radovan.scalatra.providers

import com.google.inject.{Provider, Singleton}
import com.radovan.scalatra.utils.NatsUtils

@Singleton
class NatsUtilsProvider extends Provider[NatsUtils] {

  private val natsUtils = new NatsUtils
  natsUtils.init()

  // registrujemo shutdown hook ručno
  sys.addShutdownHook {
    natsUtils.closeConnection()
  }

  override def get(): NatsUtils = natsUtils
}
