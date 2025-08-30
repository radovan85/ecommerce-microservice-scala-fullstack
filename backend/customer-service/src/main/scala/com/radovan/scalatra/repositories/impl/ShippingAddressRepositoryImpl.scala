package com.radovan.scalatra.repositories.impl

import com.radovan.scalatra.entity.ShippingAddressEntity
import com.radovan.scalatra.repositories.ShippingAddressRepository
import jakarta.inject.{Inject, Singleton}
import jakarta.persistence.criteria.{CriteriaBuilder, CriteriaQuery, Predicate, Root}
import org.hibernate.{Session, SessionFactory}

import scala.jdk.CollectionConverters._

@Singleton
class ShippingAddressRepositoryImpl extends ShippingAddressRepository {

  private var sessionFactory:SessionFactory = _

  @Inject
  private def initialize(sessionFactory: SessionFactory):Unit = {
    this.sessionFactory = sessionFactory
  }

  private def withSession[T](block: Session => T): T = {
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

  override def findById(addressId: Integer): Option[ShippingAddressEntity] = {
    withSession { session =>
      val cb: CriteriaBuilder = session.getCriteriaBuilder
      val cq: CriteriaQuery[ShippingAddressEntity] = cb.createQuery(classOf[ShippingAddressEntity])
      val root: Root[ShippingAddressEntity] = cq.from(classOf[ShippingAddressEntity])
      val predicates: Array[Predicate] = Array(cb.equal(root.get("shippingAddressId"), addressId))
      cq.where(predicates: _*)
      val results = session.createQuery(cq).getResultList.asScala.toList
      results.headOption
    }
  }

  override def save(addressEntity: ShippingAddressEntity): ShippingAddressEntity = {
    withSession { session =>
      if (addressEntity.getShippingAddressId != null) {
        session.merge(addressEntity)
      } else {
        session.persist(addressEntity)
      }
      session.flush()
      addressEntity
    }
  }

  override def findAll: Array[ShippingAddressEntity] = {
    withSession { session =>
      val cb: CriteriaBuilder = session.getCriteriaBuilder
      val cq: CriteriaQuery[ShippingAddressEntity] = cb.createQuery(classOf[ShippingAddressEntity])
      val root: Root[ShippingAddressEntity] = cq.from(classOf[ShippingAddressEntity])
      cq.select(root)

      session.createQuery(cq).getResultList.asScala.toArray
    }
  }
}
