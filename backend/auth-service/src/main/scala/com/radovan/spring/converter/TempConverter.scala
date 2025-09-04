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
    usersOpt match {
      case Some(users) =>
        users.foreach(userEntity => usersIds += userEntity.getId())
      case None =>
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
          userRepository.findById(userId) match {
            case Some(userEntity) => users += userEntity
            case None =>
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
    rolesOpt match {
      case Some(roles) => roles.foreach(roleEntity => rolesIds += roleEntity.getId())
      case None =>
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
          roleRepository.findById(roleId) match {
            case Some(roleEntity) => roles += roleEntity
            case None =>
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
