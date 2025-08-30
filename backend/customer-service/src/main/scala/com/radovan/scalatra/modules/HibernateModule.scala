package com.radovan.scalatra.modules

import com.google.inject.{AbstractModule, Provides}
import com.radovan.scalatra.utils.HibernateUtil
import org.hibernate.SessionFactory
import jakarta.inject.Singleton

class HibernateModule extends AbstractModule {
  override def configure(): Unit = {
    // Prazna konfiguracija — koristi @Provides metod ispod
  }

  @Provides
  @Singleton
  def provideSessionFactory(): SessionFactory = {
    val hibernateUtil = new HibernateUtil()
    hibernateUtil.getSessionFactory
  }
}

