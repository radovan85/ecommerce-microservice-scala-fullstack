package com.radovan.scalatra.repositories.impl

import com.radovan.scalatra.entity.CustomerEntity
import com.radovan.scalatra.repositories.CustomerRepository
import com.radovan.scalatra.services.PrometheusService
import jakarta.inject.{Inject, Singleton}
import jakarta.persistence.criteria.{CriteriaBuilder, CriteriaQuery, Predicate, Root}
import org.hibernate.{Session, SessionFactory}

import scala.jdk.CollectionConverters._

@Singleton
class CustomerRepositoryImpl extends CustomerRepository {

  private var sessionFactory:SessionFactory = _
  private var prometheusService:PrometheusService = _

  @Inject
  private def initialize(sessionFactory: SessionFactory,prometheusService: PrometheusService):Unit = {
    this.sessionFactory = sessionFactory
    this.prometheusService = prometheusService
  }

  private def withSession[T](block: Session => T): T = {
    prometheusService.updateDatabaseQueryCount()
    val session = sessionFactory.openSession()
    val transaction = session.beginTransaction()

    try {
      val result = block(session)
      transaction.commit()
      result
    } catch {
      case e: Exception =>
        transaction.rollback()
        throw e
    } finally {
      session.close()
    }
  }

  override def findByUserId(userId: Integer): Option[CustomerEntity] = {
    withSession { session =>
      val cb: CriteriaBuilder = session.getCriteriaBuilder
      val cq: CriteriaQuery[CustomerEntity] = cb.createQuery(classOf[CustomerEntity])
      val root: Root[CustomerEntity] = cq.from(classOf[CustomerEntity])
      val predicates: Array[Predicate] = Array(cb.equal(root.get("userId"), userId))
      cq.where(predicates: _*)
      val results = session.createQuery(cq).getResultList.asScala.toList
      results.headOption
    }
  }

  override def findById(customerId: Integer): Option[CustomerEntity] = {
    withSession { session =>
      val cb: CriteriaBuilder = session.getCriteriaBuilder
      val cq: CriteriaQuery[CustomerEntity] = cb.createQuery(classOf[CustomerEntity])
      val root: Root[CustomerEntity] = cq.from(classOf[CustomerEntity])
      val predicates: Array[Predicate] = Array(cb.equal(root.get("customerId"), customerId))
      cq.where(predicates: _*)
      val results = session.createQuery(cq).getResultList.asScala.toList
      results.headOption
    }
  }

  override def findAll: Array[CustomerEntity] = {
    withSession { session =>
      val cb: CriteriaBuilder = session.getCriteriaBuilder
      val cq: CriteriaQuery[CustomerEntity] = cb.createQuery(classOf[CustomerEntity])
      val root: Root[CustomerEntity] = cq.from(classOf[CustomerEntity])
      cq.select(root)

      session.createQuery(cq).getResultList.asScala.toArray
    }
  }

  override def deleteById(customerId: Integer): Unit = {
    withSession { session =>
      val customerEntity = session.get(classOf[CustomerEntity], customerId)
      if (customerEntity != null) session.remove(customerEntity)
    }
  }

  override def save(customerEntity: CustomerEntity): CustomerEntity = {
    withSession { session =>
      if (customerEntity.getCustomerId != null) {
        session.merge(customerEntity)
      } else {
        session.persist(customerEntity)
      }
      session.flush()
      customerEntity
    }
  }
}
