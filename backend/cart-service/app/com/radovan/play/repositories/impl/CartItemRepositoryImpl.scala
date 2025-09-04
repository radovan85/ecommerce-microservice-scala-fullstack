package com.radovan.play.repositories.impl

import com.radovan.play.entity.CartItemEntity
import com.radovan.play.repositories.CartItemRepository
import com.radovan.play.services.PrometheusService
import jakarta.inject.Inject
import jakarta.persistence.criteria.{CriteriaBuilder, CriteriaQuery, Predicate, Root}
import org.hibernate.{Session, SessionFactory}

import scala.jdk.CollectionConverters._

class CartItemRepositoryImpl extends CartItemRepository {

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

  override def deleteAllByCartId(cartId: Integer): Unit = {
    withSession { session =>
      val cb = session.getCriteriaBuilder
      val delete = cb.createCriteriaDelete(classOf[CartItemEntity])
      val root = delete.from(classOf[CartItemEntity])
      val predicate = cb.equal(root.get("cart").get("cartId"), cartId)
      delete.where(Array(predicate): _*)

      session.createMutationQuery(delete).executeUpdate()
    }
  }

  override def deleteAllByProductId(productId: Integer): Unit = {
    withSession { session =>
      val cb = session.getCriteriaBuilder
      val delete = cb.createCriteriaDelete(classOf[CartItemEntity])
      val root = delete.from(classOf[CartItemEntity])
      val predicate = cb.equal(root.get("productId"), productId)
      delete.where(Array(predicate): _*)

      session.createMutationQuery(delete).executeUpdate()
    }
  }

  override def findAllByCartId(cartId: Integer): Array[CartItemEntity] = {
    withSession { session =>
      val cb: CriteriaBuilder = session.getCriteriaBuilder
      val cq: CriteriaQuery[CartItemEntity] = cb.createQuery(classOf[CartItemEntity])
      val root: Root[CartItemEntity] = cq.from(classOf[CartItemEntity])

      val predicate: Predicate = cb.equal(root.get("cart").get("cartId"), cartId)
      cq.where(Array(predicate): _*)

      val query = session.createQuery(cq)
      query.getResultList.asScala.toArray
    }
  }

  override def findAllByProductId(productId: Integer): Array[CartItemEntity] = {
    withSession { session =>
      val cb = session.getCriteriaBuilder
      val cq = cb.createQuery(classOf[CartItemEntity])
      val root = cq.from(classOf[CartItemEntity])

      val predicate = cb.equal(root.get("productId"), productId)
      cq.where(Array(predicate): _*)

      session.createQuery(cq).getResultList.asScala.toArray
    }
  }

  override def findById(itemId: Integer): Option[CartItemEntity] = {
    withSession { session =>
      val cb: CriteriaBuilder = session.getCriteriaBuilder
      val cq: CriteriaQuery[CartItemEntity] = cb.createQuery(classOf[CartItemEntity])
      val root: Root[CartItemEntity] = cq.from(classOf[CartItemEntity])
      val predicate = cb.equal(root.get("cartItemId"), itemId)
      cq.where(Array(predicate): _*)

      val results = session.createQuery(cq).getResultList.asScala.toList
      results.headOption
    }
  }

  override def deleteById(itemId: Integer): Unit = {
    withSession { session =>
      val itemEntity = session.get(classOf[CartItemEntity], itemId)
      if (itemEntity != null) {
        session.remove(itemEntity)
      }
    }
  }

  override def save(itemEntity: CartItemEntity): CartItemEntity = {
    withSession { session =>
      if (itemEntity.getCartItemId() != null) {
        session.merge(itemEntity)
      } else {
        session.persist(itemEntity)
      }
      session.flush()
      itemEntity
    }
  }
}
