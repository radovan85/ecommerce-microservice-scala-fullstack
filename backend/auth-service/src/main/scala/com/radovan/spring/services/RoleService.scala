package com.radovan.spring.services

import com.radovan.spring.dto.RoleDto

trait RoleService {

  def listAllByUserId(userId:Integer):Array[RoleDto]
}
