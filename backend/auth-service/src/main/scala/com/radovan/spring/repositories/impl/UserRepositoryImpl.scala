package com.radovan.spring.repositories.impl

import com.radovan.spring.entity.UserEntity
import com.radovan.spring.repositories.UserRepository
import com.radovan.spring.services.PrometheusService
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.{EntityManager, PersistenceContext}
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

import scala.jdk.CollectionConverters._

@Repository
@Transactional
class UserRepositoryImpl extends UserRepository{

  @PersistenceContext
  private var entityManager:EntityManager = _

  @Autowired
  private var prometheusService:PrometheusService = _

  override def findAll: Array[UserEntity] = {
    prometheusService.updateDatabaseQueryCount()
    val cb = entityManager.getCriteriaBuilder
    val query = cb.createQuery(classOf[UserEntity])
    val root = query.from(classOf[UserEntity])

    query.select(root)

    val result = entityManager.createQuery(query).getResultList
    result.asScala.toArray
  }


  override def findByEmail(email: String): Option[UserEntity] = {
    prometheusService.updateDatabaseQueryCount()
    val cb = entityManager.getCriteriaBuilder
    val query = cb.createQuery(classOf[UserEntity])
    val root = query.from(classOf[UserEntity])

    val predicates: Array[Predicate] = Array(cb.equal(root.get("email"), email))
    query.where(predicates: _*)

    val result = entityManager.createQuery(query).getResultList
    result.asScala.headOption
  }

  override def findById(userId: Integer): Option[UserEntity] = {
    prometheusService.updateDatabaseQueryCount()
    val cb = entityManager.getCriteriaBuilder
    val query = cb.createQuery(classOf[UserEntity])
    val root = query.from(classOf[UserEntity])

    val predicates: Array[Predicate] = Array(cb.equal(root.get("id"), userId))
    query.where(predicates: _*)

    val result = entityManager.createQuery(query).getResultList
    result.asScala.headOption
  }



  override def save(userEntity: UserEntity): UserEntity = {
    prometheusService.updateDatabaseQueryCount()
    if (userEntity.getId() == null) {
      entityManager.persist(userEntity)
    } else {
      entityManager.merge(userEntity)
    }

    entityManager.flush()
    userEntity
  }

  override def deleteById(userId: Integer): Unit = {
    prometheusService.updateDatabaseQueryCount()
    findById(userId).foreach(entityManager.remove)
  }
}
