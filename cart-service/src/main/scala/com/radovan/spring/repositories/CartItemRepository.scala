package com.radovan.spring.repositories

import com.radovan.spring.entity.CartItemEntity
import org.springframework.data.jpa.repository.{JpaRepository, Modifying, Query}
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

import java.util

@Repository
trait CartItemRepository extends JpaRepository[CartItemEntity, Integer]{

  @Modifying
  @Query(value = "delete from cart_items where cart_id = :cartId", nativeQuery = true)
  def deleteAllByCartId(@Param("cartId") cartId:Integer):Unit

  @Query(value = "select * from cart_items where cart_id = :cartId", nativeQuery = true)
  def findAllByCartId(@Param("cartId") cartId:Integer):util.List[CartItemEntity]

  def findAllByProductId(productId:Integer):util.List[CartItemEntity]

  def deleteAllByProductId(productId:Integer):Unit
}
