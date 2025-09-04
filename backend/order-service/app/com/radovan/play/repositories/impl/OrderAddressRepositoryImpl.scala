package com.radovan.play.repositories.impl

import com.radovan.play.entity.OrderAddressEntity
import com.radovan.play.repositories.OrderAddressRepository
import com.radovan.play.services.PrometheusService
import jakarta.inject.{Inject, Singleton}
import jakarta.persistence.criteria.{CriteriaBuilder, CriteriaQuery, Predicate, Root}
import org.hibernate.{Session, SessionFactory}

import scala.jdk.CollectionConverters._

@Singleton
class OrderAddressRepositoryImpl extends OrderAddressRepository{

  private var sessionFactory: SessionFactory = _
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


  override def findById(addressId: Integer): Option[OrderAddressEntity] = {
    withSession { session =>
      val cb: CriteriaBuilder = session.getCriteriaBuilder
      val cq: CriteriaQuery[OrderAddressEntity] = cb.createQuery(classOf[OrderAddressEntity])
      val root: Root[OrderAddressEntity] = cq.from(classOf[OrderAddressEntity])
      val predicates: Array[Predicate] = Array(cb.equal(root.get("orderAddressId"), addressId))
      cq.where(predicates: _*)
      val results = session.createQuery(cq).getResultList.asScala.toList
      results.headOption
    }
  }

  override def findAll: Array[OrderAddressEntity] = {
    withSession { session =>
      val cb: CriteriaBuilder = session.getCriteriaBuilder
      val cq: CriteriaQuery[OrderAddressEntity] = cb.createQuery(classOf[OrderAddressEntity])
      val root: Root[OrderAddressEntity] = cq.from(classOf[OrderAddressEntity])
      cq.select(root)
      session.createQuery(cq).getResultList.asScala.toArray
    }
  }

  override def save(addressEntity: OrderAddressEntity): OrderAddressEntity = {
    withSession { session =>
      if (addressEntity.getOrderAddressId() == null) {
        session.persist(addressEntity)
      } else {
        session.merge(addressEntity)
      }
      session.flush()
      addressEntity
    }
  }
}
