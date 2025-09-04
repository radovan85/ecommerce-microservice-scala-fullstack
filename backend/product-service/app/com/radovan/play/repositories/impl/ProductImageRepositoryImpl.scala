package com.radovan.play.repositories.impl

import com.radovan.play.entity.ProductImageEntity
import com.radovan.play.repositories.ProductImageRepository
import com.radovan.play.services.PrometheusService
import jakarta.inject.{Inject, Singleton}
import jakarta.persistence.criteria.{CriteriaBuilder, CriteriaQuery, Predicate, Root}
import org.hibernate.{Session, SessionFactory}

import scala.jdk.CollectionConverters._

@Singleton
class ProductImageRepositoryImpl extends ProductImageRepository{

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


  override def save(imageEntity: ProductImageEntity): ProductImageEntity = {
    withSession{ session =>
      if(imageEntity.getId() == null){
        session.persist(imageEntity)
      }else {
        session.merge(imageEntity)
      }

      session.flush()
      imageEntity
    }
  }

  override def findByProductId(productId: Integer): Option[ProductImageEntity] = {
    withSession { session =>
      val cb: CriteriaBuilder = session.getCriteriaBuilder
      val cq: CriteriaQuery[ProductImageEntity] = cb.createQuery(classOf[ProductImageEntity])
      val root: Root[ProductImageEntity] = cq.from(classOf[ProductImageEntity])

      val predicates: Array[Predicate] = Array(cb.equal(root.get("product").get("productId"), productId))
      cq.where(predicates: _*)

      val results = session.createQuery(cq).getResultList.asScala.toList
      results.headOption
    }
  }

  override def findAll: Array[ProductImageEntity] = {
    withSession { session =>
      val cb: CriteriaBuilder = session.getCriteriaBuilder
      val cq: CriteriaQuery[ProductImageEntity] = cb.createQuery(classOf[ProductImageEntity])
      val root: Root[ProductImageEntity] = cq.from(classOf[ProductImageEntity])
      cq.select(root)
      session.createQuery(cq).getResultList.asScala.toArray
    }
  }

  override def deleteById(imageId: Integer): Unit = {
    withSession { session =>
      val imageEntity = session.get(classOf[ProductImageEntity], imageId)
      if(imageEntity!=null) session.remove(imageEntity)
    }
  }

  override def findById(imageId: Integer): Option[ProductImageEntity] = {
    withSession { session =>
      val cb: CriteriaBuilder = session.getCriteriaBuilder
      val cq: CriteriaQuery[ProductImageEntity] = cb.createQuery(classOf[ProductImageEntity])
      val root: Root[ProductImageEntity] = cq.from(classOf[ProductImageEntity])
      val predicates: Array[Predicate] = Array(cb.equal(root.get("id"), imageId))
      cq.where(predicates: _*)
      val results = session.createQuery(cq).getResultList.asScala.toList
      results.headOption
    }
  }
}
