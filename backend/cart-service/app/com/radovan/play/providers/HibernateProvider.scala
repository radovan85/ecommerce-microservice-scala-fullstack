package com.radovan.play.providers

import com.google.inject.Provider
import com.radovan.play.utils.HibernateUtil
import jakarta.inject.Inject
import org.hibernate.SessionFactory
import play.api.inject.ApplicationLifecycle

import scala.concurrent.Future

class HibernateProvider @Inject()(lifecycle: ApplicationLifecycle) extends Provider[SessionFactory] {
  private val hibernateUtil = new HibernateUtil
  private val sessionFactory = hibernateUtil.getSessionFactory

  lifecycle.addStopHook { () =>
    Future.successful {
      hibernateUtil.shutdown()
    }
  }

  override def get(): SessionFactory = sessionFactory
}
