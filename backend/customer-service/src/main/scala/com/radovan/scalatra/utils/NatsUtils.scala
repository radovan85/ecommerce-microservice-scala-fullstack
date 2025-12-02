package com.radovan.scalatra.utils

import io.nats.client.{Connection, Nats}
import jakarta.annotation.{PostConstruct, PreDestroy}

class NatsUtils  {

  private var nc: Connection = _

  @PostConstruct
  def init(): Unit = {
    try {
      nc = Nats.connect("nats://localhost:4222")
      println("*** NATS connection has been established!")
    } catch {
      case e: Exception =>
        System.err.println("*** Error accessing NATS server!")
        e.printStackTrace()
    }
  }

  def getConnection: Connection = nc

  @PreDestroy
  def closeConnection(): Unit = {
    try {
      if (nc != null) {
        nc.close()
        println("*** NATS connection closed!")
      }
    } catch {
      case e: Exception =>
        System.err.println("*** Error closing NATS connection!")
        e.printStackTrace()
    }
  }
}


