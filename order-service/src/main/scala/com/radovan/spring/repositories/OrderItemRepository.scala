package com.radovan.spring.repositories

import com.radovan.spring.entity.OrderItemEntity
import org.springframework.data.jpa.repository.{JpaRepository, Query}
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

import java.util

@Repository
trait OrderItemRepository extends JpaRepository[OrderItemEntity, Integer] {

  @Query(value = "select * from order_items where order_id = :orderId", nativeQuery = true)
  def listAllByOrderId(@Param("orderId") orderId:Integer):util.List[OrderItemEntity]
}
