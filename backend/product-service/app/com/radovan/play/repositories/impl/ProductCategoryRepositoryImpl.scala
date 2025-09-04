package com.radovan.play.repositories.impl

import com.radovan.play.entity.ProductCategoryEntity
import com.radovan.play.repositories.ProductCategoryRepository
import com.radovan.play.services.PrometheusService
import jakarta.inject.{Inject, Singleton}
import jakarta.persistence.criteria.{CriteriaBuilder, CriteriaQuery, Predicate, Root}
import org.hibernate.{Session, SessionFactory}

import scala.jdk.CollectionConverters._

@Singleton
class ProductCategoryRepositoryImpl extends ProductCategoryRepository{

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


  override def findAll: Array[ProductCategoryEntity] = {
    withSession { session =>
      val cb: CriteriaBuilder = session.getCriteriaBuilder
      val cq: CriteriaQuery[ProductCategoryEntity] = cb.createQuery(classOf[ProductCategoryEntity])
      val root: Root[ProductCategoryEntity] = cq.from(classOf[ProductCategoryEntity])
      cq.select(root)
      session.createQuery(cq).getResultList.asScala.toArray
    }
  }

  override def deleteById(categoryId: Integer): Unit = {
    withSession {session =>
      val categoryEntity = session.get(classOf[ProductCategoryEntity], categoryId)
      if(categoryEntity!=null) session.remove(categoryEntity)
    }
  }

  override def findById(categoryId: Integer): Option[ProductCategoryEntity] = {
    withSession { session =>
      val cb: CriteriaBuilder = session.getCriteriaBuilder
      val cq: CriteriaQuery[ProductCategoryEntity] = cb.createQuery(classOf[ProductCategoryEntity])
      val root: Root[ProductCategoryEntity] = cq.from(classOf[ProductCategoryEntity])
      val predicates: Array[Predicate] = Array(cb.equal(root.get("productCategoryId"), categoryId))
      cq.where(predicates: _*)
      val results = session.createQuery(cq).getResultList.asScala.toList
      results.headOption
    }
  }

  override def save(categoryEntity: ProductCategoryEntity): ProductCategoryEntity = {
    withSession{ session =>
      if (categoryEntity.getProductCategoryId() == null) {
        session.persist(categoryEntity)
      } else {
        session.merge(categoryEntity)
      }

      session.flush()
      categoryEntity
    }
  }

  override def findByName(name: String): Option[ProductCategoryEntity] = {
    withSession { session =>
      val cb: CriteriaBuilder = session.getCriteriaBuilder
      val cq: CriteriaQuery[ProductCategoryEntity] = cb.createQuery(classOf[ProductCategoryEntity])
      val root: Root[ProductCategoryEntity] = cq.from(classOf[ProductCategoryEntity])

      val predicates: Array[Predicate] = Array(cb.equal(root.get("name"), name))
      cq.where(predicates: _*)

      val results = session.createQuery(cq).getResultList.asScala.toList
      results.headOption
    }
  }
}
