package com.radovan.scalatra.providers

import com.google.inject.{Provider, Singleton}
import com.radovan.scalatra.utils.HibernateUtil
import org.hibernate.SessionFactory

class HibernateProvider extends Provider[SessionFactory] {

  private val hibernateUtil = new HibernateUtil
  private val sessionFactory: SessionFactory = hibernateUtil.getSessionFactory

  // registrujemo shutdown hook ručno
  sys.addShutdownHook {
    hibernateUtil.shutdown()
  }

  override def get(): SessionFactory = sessionFactory
}

