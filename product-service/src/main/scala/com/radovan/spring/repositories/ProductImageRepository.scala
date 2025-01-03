package com.radovan.spring.repositories

import com.radovan.spring.entity.ProductImageEntity
import org.springframework.data.jpa.repository.{JpaRepository, Query}
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
trait ProductImageRepository extends JpaRepository[ProductImageEntity, Integer]{

  @Query(value = "select * from product_images where product_id = :productId",nativeQuery = true)
  def findByProductId(@Param("productId") productId:Integer):Option[ProductImageEntity]
}