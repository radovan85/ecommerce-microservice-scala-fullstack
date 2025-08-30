package com.radovan.scalatra.repositories

import com.radovan.scalatra.entity.CustomerEntity

trait CustomerRepository {

  def findByUserId(userId: Integer): Option[CustomerEntity]

  def findById(customerId: Integer): Option[CustomerEntity]

  def findAll: Array[CustomerEntity]

  def deleteById(customerId: Integer): Unit

  def save(customerEntity: CustomerEntity): CustomerEntity

}