package com.radovan.play.repositories.impl

import com.radovan.play.entity.OrderEntity
import com.radovan.play.repositories.OrderRepository
import jakarta.inject.{Inject, Singleton}
import jakarta.persistence.criteria.{CriteriaBuilder, CriteriaQuery, Predicate, Root}
import org.hibernate.{Session, SessionFactory}

import scala.jdk.CollectionConverters._

@Singleton
class OrderRepositoryImpl extends OrderRepository{

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


  override def findById(orderId: Integer): Option[OrderEntity] = {
    withSession { session =>
      val cb: CriteriaBuilder = session.getCriteriaBuilder
      val cq: CriteriaQuery[OrderEntity] = cb.createQuery(classOf[OrderEntity])
      val root: Root[OrderEntity] = cq.from(classOf[OrderEntity])
      val predicates: Array[Predicate] = Array(cb.equal(root.get("orderId"), orderId))
      cq.where(predicates: _*)
      val results = session.createQuery(cq).getResultList.asScala.toList
      results.headOption
    }
  }

  override def deleteById(orderId: Integer): Unit = {
    withSession { session =>
      val order = session.get(classOf[OrderEntity], orderId)
      if (order != null) {
        session.remove(order)
      }
    }
  }

  override def findAllByCartId(cartId: Integer): Array[OrderEntity] = {
    withSession { session =>
      val cb: CriteriaBuilder = session.getCriteriaBuilder
      val cq: CriteriaQuery[OrderEntity] = cb.createQuery(classOf[OrderEntity])
      val root: Root[OrderEntity] = cq.from(classOf[OrderEntity])

      val predicate: Predicate = cb.equal(root.get("cartId"), cartId)
      cq.where(Array(predicate): _*) // ← eksplicitno koristi varargs verziju

      val query = session.createQuery(cq)
      query.getResultList.asScala.toArray
    }
  }



  override def findAll: Array[OrderEntity] = {
    withSession { session =>
      val cb: CriteriaBuilder = session.getCriteriaBuilder
      val cq: CriteriaQuery[OrderEntity] = cb.createQuery(classOf[OrderEntity])
      val root: Root[OrderEntity] = cq.from(classOf[OrderEntity])
      cq.select(root)
      session.createQuery(cq).getResultList.asScala.toArray
    }
  }

  override def save(orderEntity: OrderEntity): OrderEntity = {
    withSession { session =>
      if (orderEntity.getOrderId() == null) {
        session.persist(orderEntity)
      } else {
        session.merge(orderEntity)
      }
      session.flush()
      orderEntity
    }
  }
}
