package com.radovan.spring.data

import com.radovan.spring.entity.RoleEntity
import com.radovan.spring.entity.UserEntity
import com.radovan.spring.repositories.{RoleRepository, UserRepository}
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.util


@Component
class LoadData {

  private var roleRepository: RoleRepository = _
  private var userRepository: UserRepository = _
  private var passwordEncoder: BCryptPasswordEncoder = _

  @Autowired
  def this(roleRepository: RoleRepository, userRepository: UserRepository
           , passwordEncoder: BCryptPasswordEncoder) {

    this()
    this.roleRepository = roleRepository
    this.userRepository = userRepository
    this.passwordEncoder = passwordEncoder
    addRolesData()
    addAdminData()
  }

  def addRolesData(): Unit = {
    val role1: Option[RoleEntity] = roleRepository.findByRole("ADMIN")
    val role2: Option[RoleEntity] = roleRepository.findByRole("ROLE_USER")

    role1 match {
      case Some(_) =>
      case None => roleRepository.save(new RoleEntity("ADMIN"))
    }

    role2 match {
      case Some(_) =>
      case None => roleRepository.save(new RoleEntity("ROLE_USER"))
    }
  }


  def addAdminData(): Unit = {
    val role: Option[RoleEntity] = roleRepository.findByRole("ADMIN")
    role.foreach { roleOpt =>
      val roles: util.List[RoleEntity] = new util.ArrayList[RoleEntity]
      roles.add(roleOpt)
      val userEntity: Option[UserEntity] = userRepository.findByEmail("doe@luv2code.com")
      userEntity match {
        case Some(_) => println("Admin already added")
        case None =>
          val adminEntity: UserEntity = new UserEntity("John", "Doe", "doe@luv2code.com", "admin123", 1.toByte)
          val password = adminEntity.getPassword
          adminEntity.setPassword(passwordEncoder.encode(password))
          adminEntity.setRoles(roles)
          val storedAdmin = userRepository.save(adminEntity)
          val users = new util.ArrayList[UserEntity]
          users.add(storedAdmin)
          val roleAdmin: RoleEntity = roleOpt
          roleAdmin.setUsers(users)
          roleRepository.saveAndFlush(roleAdmin)
      }
    }
  }


}
