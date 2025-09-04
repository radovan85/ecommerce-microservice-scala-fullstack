package com.radovan.play.modules

import com.google.inject.{AbstractModule, Provides, Singleton}
import com.radovan.play.utils.HibernateUtil
import org.hibernate.SessionFactory

class HibernateModule extends AbstractModule {
  override def configure(): Unit = {

  }

  @Provides
  @Singleton
  def provideSessionFactory(): SessionFactory = {
    val hibernateUtil = new HibernateUtil()
    hibernateUtil.getSessionFactory
  }
}