package com.radovan.spring.repositories

import com.radovan.spring.entity.RoleEntity

trait RoleRepository {

  def findByRole(role:String):Option[RoleEntity]

  def findAllByUserId(userId:Integer):Array[RoleEntity]

  def save(roleEntity: RoleEntity):RoleEntity

  def findById(roleId:Integer):Option[RoleEntity]
}
