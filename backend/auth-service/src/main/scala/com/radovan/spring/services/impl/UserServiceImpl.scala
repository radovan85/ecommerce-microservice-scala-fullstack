package com.radovan.spring.services.impl

import com.radovan.spring.converter.TempConverter
import com.radovan.spring.dto.UserDto
import com.radovan.spring.entity.{RoleEntity, UserEntity}
import com.radovan.spring.exceptions.{ExistingInstanceException, InstanceUndefinedException, OperationNotAllowedException}
import com.radovan.spring.repositories.{RoleRepository, UserRepository}
import com.radovan.spring.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.{AuthenticationManager, UsernamePasswordAuthenticationToken}
import org.springframework.security.core.{Authentication, AuthenticationException}
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

import scala.collection.mutable.ArrayBuffer
import scala.jdk.CollectionConverters._

@Service
class UserServiceImpl extends UserService {

  private var userRepository: UserRepository = _
  private var roleRepository: RoleRepository = _
  private var tempConverter: TempConverter = _
  private var passwordEncoder: BCryptPasswordEncoder = _
  private var authenticationManager: AuthenticationManager = _

  @Autowired
  private def initialize(userRepository: UserRepository, roleRepository: RoleRepository,
                         tempConverter: TempConverter, passwordEncoder: BCryptPasswordEncoder,
                         authenticationManager: AuthenticationManager): Unit = {
    this.userRepository = userRepository
    this.roleRepository = roleRepository
    this.tempConverter = tempConverter
    this.passwordEncoder = passwordEncoder
    this.authenticationManager = authenticationManager
  }


  override def listAll: Array[UserDto] = {
    userRepository.findAll.collect {
      case userEntity => tempConverter.userEntityToDto(userEntity)
    }
  }


  override def getCurrentUser: UserDto = {
    val authentication = SecurityContextHolder.getContext.getAuthentication
    if (authentication.isAuthenticated) {
      val currentUsername = authentication.getName
      userRepository.findByEmail(currentUsername) match {
        case Some(userEntity) => tempConverter.userEntityToDto(userEntity)
        case None => throw new InstanceUndefinedException(new Error("Invalid user!"))
      }
    } else {
      throw new InstanceUndefinedException(new Error("Invalid user!"))
    }
  }

  override def getUserById(userId: Integer): UserDto = {
    userRepository.findById(userId) match {
      case Some(userEntity) => tempConverter.userEntityToDto(userEntity)
      case None => throw new InstanceUndefinedException(new Error("Invalid user!"))
    }
  }



  override def getUserByEmail(email: String): UserDto = {
    userRepository.findByEmail(email) match {
      case Some(userEntity) => tempConverter.userEntityToDto(userEntity)
      case None => throw new InstanceUndefinedException(new Error("Invalid user!"))
    }
  }



  override def authenticateUser(username: String, password: String): Option[Authentication] = {
    val authReq = new UsernamePasswordAuthenticationToken(username, password)
    val userOptional = userRepository.findByEmail(username)
    userOptional.flatMap { user =>
      try {
        val auth = authenticationManager.authenticate(authReq)
        Some(auth)
      } catch {
        case _: AuthenticationException => None
      }
    }
  }


  override def isAdmin(userId: Integer): Boolean = {
    val user = getUserById(userId)
    roleRepository.findByRole("ROLE_ADMIN") match {
      case Some(roleAdmin) =>
        val rolesIds = user.getRolesIds
        rolesIds.contains(roleAdmin.getId)
      case None =>
        false
    }
  }


  override def suspendUser(userId: Integer): Unit = {
    val user = getUserById(userId)
    if (isAdmin(user.getId())) {
      throw new OperationNotAllowedException(new Error("This operation is not allowed! The user has Admin authority!"))
    }
    user.setEnabled(0.toShort)
    userRepository.save(tempConverter.userDtoToEntity(user))
  }

  override def reactivateUser(userId: Integer): Unit = {
    val user = getUserById(userId)
    if (isAdmin(user.getId())) {
      throw new OperationNotAllowedException(
        new Error("This operation is not allowed! The user has Admin authority!")
      )
    }
    user.setEnabled(1.toShort)
    userRepository.save(tempConverter.userDtoToEntity(user))
  }


  override def addUser(user: UserDto): UserDto = {
    // Proveri da li korisnik već postoji prema email adresi
    userRepository.findByEmail(user.getEmail) match {
      case Some(_) => throw new ExistingInstanceException(new Error("This email exists already!"))
      case None => // Nastavlja dalje ako korisnik ne postoji
    }

    // Pronalazak RoleEntity za "ROLE_USER"
    val roleEntity: RoleEntity = roleRepository.findByRole("ROLE_USER") match {
      case Some(roleEntity) => roleEntity
      case None => throw new InstanceUndefinedException(new Error("The role has not been found!"))
    }

    // Kreiranje i podešavanje UserEntity objekta
    val roles = new ArrayBuffer[RoleEntity]()
    roles += roleEntity
    user.setPassword(passwordEncoder.encode(user.getPassword))
    user.setEnabled(1.asInstanceOf[Short])

    val userEntity = tempConverter.userDtoToEntity(user)
    userEntity.setRoles(roles.asJava)

    val storedUser = userRepository.save(userEntity)


    // Dodavanje korisnika u listu korisnika uloge
    //val users = Option(roleEntity.getUsers).map(_.asScala).getOrElse(new ArrayBuffer[UserEntity]())

    // Vraća konvertovani UserDto
    tempConverter.userEntityToDto(storedUser)
  }


  override def deleteUser(userId: Integer): Unit = {
    getUserById(userId) // validacija da korisnik postoji
    if (isAdmin(userId)) {
      throw new OperationNotAllowedException(
        new Error("This operation is not allowed! The user has Admin authority!")
      )
    }
    userRepository.deleteById(userId)
  }
}
