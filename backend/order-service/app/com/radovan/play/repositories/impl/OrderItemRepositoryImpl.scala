package com.radovan.play.repositories.impl

import com.radovan.play.entity.OrderItemEntity
import com.radovan.play.repositories.OrderItemRepository
import jakarta.inject.{Inject, Singleton}
import jakarta.persistence.criteria.{CriteriaBuilder, CriteriaQuery, Predicate, Root}
import org.hibernate.{Session, SessionFactory}

import scala.jdk.CollectionConverters._

@Singleton
class OrderItemRepositoryImpl extends OrderItemRepository {

  private var sessionFactory: SessionFactory = _

  @Inject
  private def initialize(sessionFactory: SessionFactory): Unit = {
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


  override def findById(itemId: Integer): Option[OrderItemEntity] = {
    withSession { session =>
      val cb: CriteriaBuilder = session.getCriteriaBuilder
      val cq: CriteriaQuery[OrderItemEntity] = cb.createQuery(classOf[OrderItemEntity])
      val root: Root[OrderItemEntity] = cq.from(classOf[OrderItemEntity])
      val predicates: Array[Predicate] = Array(cb.equal(root.get("orderItemId"), itemId))
      cq.where(predicates: _*)
      val results = session.createQuery(cq).getResultList.asScala.toList
      results.headOption
    }
  }

  override def findAllByOrderId(orderId: Integer): Array[OrderItemEntity] = {
    withSession { session =>
      val cb: CriteriaBuilder = session.getCriteriaBuilder
      val cq: CriteriaQuery[OrderItemEntity] = cb.createQuery(classOf[OrderItemEntity])
      val root: Root[OrderItemEntity] = cq.from(classOf[OrderItemEntity])

      val predicate: Predicate = cb.equal(root.get("order").get("orderId"), orderId)
      cq.where(Array(predicate): _*)

      val query = session.createQuery(cq)
      query.getResultList.asScala.toArray
    }
  }

  override def save(itemEntity: OrderItemEntity): OrderItemEntity = {
    withSession { session =>
      if (itemEntity.getOrderItemId() == null) {
        session.persist(itemEntity)
      } else {
        session.merge(itemEntity)
      }
      session.flush()
      itemEntity
    }
  }
}
