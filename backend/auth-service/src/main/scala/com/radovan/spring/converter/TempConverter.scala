package com.radovan.spring.converter

import com.radovan.spring.dto.{RoleDto, UserDto}
import com.radovan.spring.entity.{RoleEntity, UserEntity}
import com.radovan.spring.repositories.{RoleRepository, UserRepository}
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import scala.jdk.CollectionConverters._
import scala.collection.mutable.ArrayBuffer

@Component
class TempConverter {

  private var mapper:ModelMapper = _
  private var userRepository:UserRepository = _
  private var roleRepository:RoleRepository = _

  def roleEntityToDto(role: RoleEntity): RoleDto = {
    val returnValue = mapper.map(role, classOf[RoleDto])
    val usersOpt = Option(role.getUsers.asScala)
    val usersIds = new ArrayBuffer[Integer]()
    if (usersOpt.isDefined) {
      usersOpt.get.foreach(userEntity => usersIds += userEntity.getId)
    }

    returnValue.setUsersIds(usersIds.toArray)
    returnValue
  }

  def roleDtoToEntity(role: RoleDto): RoleEntity = {
    val returnValue = mapper.map(role, classOf[RoleEntity])
    val usersIdsOpt = Option(role.getUsersIds)
    val users = new ArrayBuffer[UserEntity]()
    usersIdsOpt match {
      case Some(usersIds) =>
        usersIds.foreach(userId => {
          val userEntity = userRepository.findById(userId).orElse(null)
          if (userEntity != null) {
            users += userEntity
          }
        })
      case None =>
    }

    returnValue.setUsers(users.asJava)
    returnValue
  }

  def userEntityToDto(user: UserEntity): UserDto = {
    val returnValue = mapper.map(user, classOf[UserDto])
    val rolesOpt = Option(user.getRoles.asScala)
    val rolesIds = new ArrayBuffer[Integer]()
    if (rolesOpt.isDefined) {
      rolesOpt.get.foreach(role => rolesIds += role.getId)
    }

    val enabledOpt = Option(user.getEnabled)
    enabledOpt match {
      case Some(enabled) => returnValue.setEnabled(enabled.asInstanceOf[Short])
      case None =>
    }

    returnValue.setRolesIds(rolesIds.toArray)
    returnValue
  }

  def userDtoToEntity(user: UserDto): UserEntity = {
    val returnValue = mapper.map(user, classOf[UserEntity])
    val rolesIdsOpt = Option(user.getRolesIds)
    val roles = new ArrayBuffer[RoleEntity]()
    rolesIdsOpt match {
      case Some(rolesIds) =>
        rolesIds.foreach(roleId => {
          val roleEntity = roleRepository.findById(roleId).orElse(null)
          if (roleEntity != null) {
            roles += roleEntity
          }
        })
      case None =>
    }

    val enabledOpt = Option(user.getEnabled)
    enabledOpt match {
      case Some(enabled) => returnValue.setEnabled(enabled.asInstanceOf[Byte])
      case None =>
    }

    returnValue.setRoles(roles.asJava)
    returnValue
  }

  @Autowired
  private def initialize(mapper: ModelMapper,userRepository: UserRepository,roleRepository: RoleRepository):Unit = {
    this.mapper = mapper
    this.userRepository = userRepository
    this.roleRepository = roleRepository
  }

}
