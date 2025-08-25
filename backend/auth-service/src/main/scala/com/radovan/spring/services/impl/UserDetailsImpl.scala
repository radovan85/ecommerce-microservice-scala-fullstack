package com.radovan.spring.services.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import com.radovan.spring.converter.TempConverter
import com.radovan.spring.dto.UserDto
import com.radovan.spring.entity.UserEntity
import com.radovan.spring.services.UserService

@Service
class UserDetailsImpl extends UserDetailsService {

  private var userService: UserService = _
  private var tempConverter: TempConverter = _

  @Autowired
  private def initialize(userService: UserService, tempConverter: TempConverter): Unit = {
    this.userService = userService
    this.tempConverter = tempConverter
  }

  override def loadUserByUsername(name: String): UserEntity = {
    val userDto: UserDto = userService.getUserByEmail(name)
    tempConverter.userDtoToEntity(userDto)
  }
}
