package com.radovan.spring.repositories

import com.radovan.spring.entity.ProductCategoryEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
trait ProductCategoryRepository extends JpaRepository[ProductCategoryEntity, Integer]{

  def findByName(name:String):Option[ProductCategoryEntity]
}
