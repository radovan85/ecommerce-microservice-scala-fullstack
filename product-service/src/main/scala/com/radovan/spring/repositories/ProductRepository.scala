package com.radovan.spring.repositories

import com.radovan.spring.entity.ProductEntity
import org.springframework.data.jpa.repository.{JpaRepository, Query}
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

import java.util

@Repository
trait ProductRepository extends JpaRepository[ProductEntity, Integer]{

  @Query(value = "select * from products where category_id = :categoryId", nativeQuery = true)
  def findAllByCategoryId(@Param("categoryId") categoryId:Integer):util.List[ProductEntity]

}
