package com.radovan.spring.repositories

import com.radovan.spring.entity.UserEntity

trait UserRepository {

  def findAll: Array[UserEntity]

  def findById(userId:Integer):Option[UserEntity]

  def findByEmail(email:String):Option[UserEntity]

  def save(userEntity: UserEntity):UserEntity

  def deleteById(userId:Integer):Unit
}
