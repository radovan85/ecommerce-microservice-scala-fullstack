package com.radovan.spring.repositories.impl

import com.radovan.spring.entity.RoleEntity
import com.radovan.spring.repositories.RoleRepository
import com.radovan.spring.services.PrometheusService
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.{EntityManager, PersistenceContext}
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

import scala.jdk.CollectionConverters._

@Repository
@Transactional
class RoleRepositoryImpl extends RoleRepository{

  @PersistenceContext
  private var entityManager:EntityManager = _

  @Autowired
  private var prometheusService:PrometheusService = _

  override def findByRole(role: String): Option[RoleEntity] = {
    prometheusService.updateDatabaseQueryCount()
    val cb = entityManager.getCriteriaBuilder
    val query = cb.createQuery(classOf[RoleEntity])
    val root = query.from(classOf[RoleEntity])

    val predicates: Array[Predicate] = Array(cb.equal(root.get("role"), role))
    query.where(predicates: _*)

    val result = entityManager.createQuery(query).getResultList
    result.asScala.headOption
  }

  override def findAllByUserId(userId: Integer): Array[RoleEntity] = {
    prometheusService.updateDatabaseQueryCount()
    val cb = entityManager.getCriteriaBuilder
    val query = cb.createQuery(classOf[RoleEntity])
    val root = query.from(classOf[RoleEntity])

    val usersJoin = root.join("users")
    val predicates: Array[Predicate] = Array(cb.equal(usersJoin.get("id"), userId))

    query.select(root).where(predicates: _*)

    val result = entityManager.createQuery(query).getResultList
    result.asScala.toArray
  }




  override def save(roleEntity: RoleEntity): RoleEntity = {
    prometheusService.updateDatabaseQueryCount()
    if (roleEntity.getId() == null) {
      entityManager.persist(roleEntity)
    } else {
      entityManager.merge(roleEntity)
    }

    entityManager.flush()
    roleEntity
  }

  override def findById(roleId: Integer): Option[RoleEntity] = {
    prometheusService.updateDatabaseQueryCount()
    val cb = entityManager.getCriteriaBuilder
    val query = cb.createQuery(classOf[RoleEntity])
    val root = query.from(classOf[RoleEntity])

    val predicates: Array[Predicate] = Array(cb.equal(root.get("id"), roleId))
    query.where(predicates: _*)

    val result = entityManager.createQuery(query).getResultList
    result.asScala.headOption
  }
}
