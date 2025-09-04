package com.radovan.play.repositories.impl

import com.radovan.play.entity.{CartEntity, CartItemEntity}
import com.radovan.play.repositories.CartRepository
import com.radovan.play.services.PrometheusService
import jakarta.inject.Inject
import jakarta.persistence.criteria.{CriteriaBuilder, CriteriaQuery, Predicate, Root}
import org.hibernate.{Session, SessionFactory}

import scala.jdk.CollectionConverters._

class CartRepositoryImpl extends CartRepository {

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

  override def calculateCartPrice(cartId: Integer): Option[Float] = {
    withSession { session =>
      val cb: CriteriaBuilder = session.getCriteriaBuilder
      val cq: CriteriaQuery[Float] = cb.createQuery(classOf[Float])
      val root = cq.from(classOf[CartItemEntity])

      val predicate = cb.equal(root.get("cart").get("cartId"), cartId)
      val sumExpr = cb.sum(root.get("price"))

      cq.select(sumExpr).where(Array(predicate): _*)

      val result = session.createQuery(cq).getSingleResult
      Option(result)
    }
  }



  override def findById(cartId: Integer): Option[CartEntity] = {
    withSession { session =>
      val cb: CriteriaBuilder = session.getCriteriaBuilder
      val cq: CriteriaQuery[CartEntity] = cb.createQuery(classOf[CartEntity])
      val root: Root[CartEntity] = cq.from(classOf[CartEntity])
      val predicates: Array[Predicate] = Array(cb.equal(root.get("cartId"), cartId))
      cq.where(predicates: _*)
      val results = session.createQuery(cq).getResultList.asScala.toList
      results.headOption
    }
  }

  override def save(cartEntity: CartEntity): CartEntity = {
    withSession { session =>
      if (cartEntity.getCartId() != null) {
        session.merge(cartEntity)
      } else {
        session.persist(cartEntity)
      }
      session.flush()
      cartEntity
    }
  }

  override def findAll: Array[CartEntity] = {
    withSession { session =>
      val cb: CriteriaBuilder = session.getCriteriaBuilder
      val cq: CriteriaQuery[CartEntity] = cb.createQuery(classOf[CartEntity])
      val root: Root[CartEntity] = cq.from(classOf[CartEntity])
      cq.select(root)

      session.createQuery(cq).getResultList.asScala.toArray
    }
  }

  override def deleteById(cartId: Integer): Unit = {
    withSession { session =>
      val cartEntity = session.get(classOf[CartEntity], cartId)
      if (cartEntity != null) session.remove(cartEntity)
    }
  }
}
