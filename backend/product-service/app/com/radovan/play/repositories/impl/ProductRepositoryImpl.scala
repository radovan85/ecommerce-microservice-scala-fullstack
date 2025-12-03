package com.radovan.play.repositories.impl

import com.radovan.play.entity.ProductEntity
import com.radovan.play.repositories.ProductRepository
import com.radovan.play.services.PrometheusService
import jakarta.inject.{Inject, Singleton}
import jakarta.persistence.criteria.{CriteriaBuilder, CriteriaQuery, Predicate, Root}
import org.hibernate.{Session, SessionFactory}

import scala.jdk.CollectionConverters._

@Singleton
class ProductRepositoryImpl extends ProductRepository{

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

  override def findById(productId: Integer): Option[ProductEntity] = {
    withSession { session =>
      val cb: CriteriaBuilder = session.getCriteriaBuilder
      val cq: CriteriaQuery[ProductEntity] = cb.createQuery(classOf[ProductEntity])
      val root: Root[ProductEntity] = cq.from(classOf[ProductEntity])
      val predicates: Array[Predicate] = Array(cb.equal(root.get("productId"), productId))
      cq.where(predicates: _*)
      val results = session.createQuery(cq).getResultList.asScala.toList
      results.headOption
    }
  }

  override def save(productEntity: ProductEntity): ProductEntity = {
    withSession { session =>
      if (productEntity.getProductId() == null) {
        session.persist(productEntity)
      } else {
        session.merge(productEntity)
      }
      session.flush()
      productEntity
    }
  }

  override def deleteById(productId: Integer): Unit = {
    withSession { session =>
      val product = session.find(classOf[ProductEntity], productId)
      if (product != null) {
        session.remove(product)
      }
    }
  }

  override def findAll: Array[ProductEntity] = {
    withSession { session =>
      val cb: CriteriaBuilder = session.getCriteriaBuilder
      val cq: CriteriaQuery[ProductEntity] = cb.createQuery(classOf[ProductEntity])
      val root: Root[ProductEntity] = cq.from(classOf[ProductEntity])
      cq.select(root)
      session.createQuery(cq).getResultList.asScala.toArray
    }
  }

  override def findAllByCategoryId(categoryId: Integer): Array[ProductEntity] = {
    withSession { session =>
      val cb: CriteriaBuilder = session.getCriteriaBuilder
      val cq: CriteriaQuery[ProductEntity] = cb.createQuery(classOf[ProductEntity])
      val root: Root[ProductEntity] = cq.from(classOf[ProductEntity])

      val predicate: Predicate = cb.equal(root.get("productCategory").get("productCategoryId"), categoryId)
      cq.where(Array(predicate): _*)

      val query = session.createQuery(cq)
      query.getResultList.asScala.toArray
    }
  }
}
