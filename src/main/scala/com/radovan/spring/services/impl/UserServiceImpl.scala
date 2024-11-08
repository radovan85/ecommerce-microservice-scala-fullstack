package com.radovan.spring.services.impl

import com.radovan.spring.converter.TempConverter
import com.radovan.spring.dto.UserDto
import com.radovan.spring.entity.{RoleEntity, UserEntity}
import com.radovan.spring.exceptions.{ExistingInstanceException, InstanceUndefinedException}
import com.radovan.spring.repositories.{RoleRepository, UserRepository}
import com.radovan.spring.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.{AuthenticationManager, UsernamePasswordAuthenticationToken}
import org.springframework.security.core.{Authentication, AuthenticationException}
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import scala.collection.JavaConverters._
import scala.collection.mutable.ArrayBuffer

@Service
class UserServiceImpl extends UserService{

  private var userRepository:UserRepository = _
  private var tempConverter:TempConverter = _
  private var roleRepository:RoleRepository = _
  private var authenticationManager: AuthenticationManager = _
  private var passwordEncoder:BCryptPasswordEncoder = _

  @Transactional(readOnly = true)
  override def getUserById(userId: Integer): UserDto =
    userRepository.findById(userId).map[UserDto](tempConverter.userEntityToDto)
      .orElseThrow(() => new InstanceUndefinedException(new Error("Invalid user!")))

  @Transactional(readOnly = true)
  override def listAllUsers: Array[UserDto] = {
    val allUsers = userRepository.findAll().asScala
    allUsers.collect {
      case userEntity => tempConverter.userEntityToDto(userEntity)
    }.toArray
  }

  @Transactional(readOnly = true)
  override def getUserByEmail(email: String): UserDto = {
    userRepository.findByEmail(email)
      .map(tempConverter.userEntityToDto)
      .fold(throw new InstanceUndefinedException(new Error("Invalid user!")))(identity)
  }

  @Transactional(readOnly = true)
  override def getCurrentUser: UserDto = {
    val authentication = SecurityContextHolder.getContext.getAuthentication
    if (authentication.isAuthenticated) {
      val currentUsername = authentication.getName
      userRepository.findByEmail(currentUsername)
        .map(tempConverter.userEntityToDto)
        .getOrElse(throw new InstanceUndefinedException(new Error("Invalid user!")))
    } else {
      throw new InstanceUndefinedException(new Error("Invalid user!"))
    }
  }

  @Transactional
  override def suspendUser(userId: Integer): Unit = {
    val user = getUserById(userId)
    user.setEnabled(0)
    userRepository.saveAndFlush(tempConverter.userDtoToEntity(user))
  }

  @Transactional
  override def clearSuspension(userId: Integer): Unit = {
    val user = getUserById(userId)
    user.setEnabled(1)
    userRepository.saveAndFlush(tempConverter.userDtoToEntity(user))
  }

  @Transactional(readOnly = true)
  override def isAdmin: Boolean = {
    val authUser = getCurrentUser
    roleRepository.findByRole("ADMIN") match {
      case Some(role) => authUser.getRolesIds.contains(role.getId)
      case None => false
    }
  }

  @Transactional(readOnly = true)
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

  @Transactional
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
    val users = Option(roleEntity.getUsers).map(_.asScala).getOrElse(new ArrayBuffer[UserEntity]())
    users += storedUser
    roleEntity.setUsers(users.asJava)
    roleRepository.saveAndFlush(roleEntity)

    // Vraća konvertovani UserDto
    tempConverter.userEntityToDto(storedUser)
  }


  @Transactional
  override def deleteUser(userId: Integer): Unit = {
    getUserById(userId)
    userRepository.deleteById(userId)
    userRepository.flush()
  }

  @Autowired
  private def initialize(userRepository: UserRepository, tempConverter: TempConverter, roleRepository: RoleRepository,
                         authenticationManager: AuthenticationManager, passwordEncoder: BCryptPasswordEncoder): Unit = {
    this.userRepository = userRepository
    this.tempConverter = tempConverter
    this.roleRepository = roleRepository
    this.authenticationManager = authenticationManager
    this.passwordEncoder = passwordEncoder
  }
}
