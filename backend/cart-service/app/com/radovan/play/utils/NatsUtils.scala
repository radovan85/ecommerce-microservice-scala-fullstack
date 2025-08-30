package com.radovan.play.utils

import io.nats.client.{Connection, Nats}
import jakarta.inject.Singleton

@Singleton
class NatsUtils {

  private val nc: Connection = try {
    val conn = Nats.connect("nats://localhost:4222")
    println("*** NATS connection has been established!")
    conn
  } catch {
    case e: Exception =>
      System.err.println("*** Error accessing NATS server!")
      e.printStackTrace()
      null
  }

  def getConnection: Connection = nc
}

