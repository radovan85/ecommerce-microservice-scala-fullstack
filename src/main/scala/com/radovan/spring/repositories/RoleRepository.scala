package com.radovan.spring.repositories

import java.util
import com.radovan.spring.entity.RoleEntity
import org.springframework.data.jpa.repository.{JpaRepository, Query}
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
trait RoleRepository extends JpaRepository[RoleEntity, Integer]{

  def findByRole(role:String):Option[RoleEntity]

  @Query(value = "SELECT r.* FROM roles r " + "JOIN users_roles ur ON r.id = ur.roles_id " + "WHERE ur.user_id = :userId", nativeQuery = true)
  def findAllByUserId(@Param("userId") userId: Integer): util.List[RoleEntity]
}
