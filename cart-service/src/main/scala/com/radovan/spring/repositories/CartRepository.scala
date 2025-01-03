package com.radovan.spring.repositories

import com.radovan.spring.entity.CartEntity
import org.springframework.data.jpa.repository.{JpaRepository, Query}
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
trait CartRepository extends JpaRepository[CartEntity, Integer]{

  @Query(value = "select sum(price) from cart_items where cart_id = :cartId", nativeQuery = true)
  def calculateCartPrice(@Param("cartId") cartId:Integer):Option[Float]
}
