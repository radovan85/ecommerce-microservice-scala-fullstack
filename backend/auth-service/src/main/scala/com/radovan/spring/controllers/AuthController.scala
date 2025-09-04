package com.radovan.spring.controllers

import com.radovan.spring.converter.TempConverter
import com.radovan.spring.dto.UserDto
import com.radovan.spring.entity.UserEntity
import com.radovan.spring.services.{RoleService, UserService}
import com.radovan.spring.utils.{AuthenticationRequest, JwtUtil}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation._

import javax.security.auth.login.CredentialNotFoundException

@RestController
@RequestMapping(Array("/api/auth"))
class AuthController {

  private var userService: UserService = _
  private var tempConverter: TempConverter = _
  private var jwtUtil: JwtUtil = _
  private var roleService: RoleService = _

  @Autowired
  private def initialize(
                          userService: UserService,
                          tempConverter: TempConverter,
                          jwtUtil: JwtUtil,
                          roleService: RoleService
                        ): Unit = {
    this.userService = userService
    this.tempConverter = tempConverter
    this.jwtUtil = jwtUtil
    this.roleService = roleService
  }

  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  @GetMapping(Array("/users"))
  def getAllUsers: ResponseEntity[Array[UserDto]] = {
    new ResponseEntity(userService.listAll, HttpStatus.OK)
  }

  @GetMapping(Array("/me"))
  def getMyData: ResponseEntity[UserDto] = {
    new ResponseEntity(userService.getCurrentUser, HttpStatus.OK)
  }

  @PostMapping(Array("/login"))
  @throws[Exception]
  def createAuthenticationToken(@RequestBody authenticationRequest: AuthenticationRequest): ResponseEntity[UserDto] = {
    val authOptional =
      userService.authenticateUser(authenticationRequest.getUsername, authenticationRequest.getPassword)

    if (authOptional.isEmpty) {
      throw new CredentialNotFoundException("Invalid username or password!")
    }

    val userDto: UserDto = userService.getUserByEmail(authenticationRequest.getUsername)
    val userDetails: UserEntity = tempConverter.userDtoToEntity(userDto)

    val userRoles = roleService.listAllByUserId(userDto.getId())
    val roleNames: List[String] = userRoles.toList.map(_.getRole)

    val jwt = jwtUtil.generateToken(userDto.getEmail, roleNames)

    val authUser = tempConverter.userEntityToDto(userDetails)
    authUser.setAuthToken(jwt)

    new ResponseEntity(authUser, HttpStatus.OK)
  }

  @GetMapping(Array("/public-key"))
  def getPublicKey: ResponseEntity[String] = {
    new ResponseEntity(jwtUtil.getPublicKeyAsPEM, HttpStatus.OK)
  }
}
