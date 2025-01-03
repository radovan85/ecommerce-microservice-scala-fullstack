package com.radovan.spring.controllers

import com.radovan.spring.converter.TempConverter
import com.radovan.spring.dto.{RoleDto, UserDto}
import com.radovan.spring.exceptions.DataNotValidatedException
import com.radovan.spring.services.{RoleService, UserService}
import com.radovan.spring.utils.{AuthenticationRequest, JwtUtil}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.Errors
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.{DeleteMapping, GetMapping, PathVariable, PostMapping, PutMapping, RequestBody, RequestHeader, RestController}

import javax.security.auth.login.CredentialNotFoundException
import scala.collection.mutable.ArrayBuffer

@RestController
class MainController {

  private var userService: UserService = _
  private var jwtTokenUtil: JwtUtil = _
  private var tempConverter: TempConverter = _
  private var roleService: RoleService = _
  private val serviceList: ArrayBuffer[String] = ArrayBuffer("order-service", "customer-service", "cart-service", "product-service")

  @Autowired
  private def initialize(userService: UserService,jwtTokenUtil:JwtUtil,
                         tempConverter: TempConverter,roleService: RoleService):Unit = {
    this.userService = userService
    this.jwtTokenUtil = jwtTokenUtil
    this.tempConverter = tempConverter
    this.roleService = roleService
  }

  @PostMapping(Array("/addUser"))
  def createUser(@Validated @RequestBody user: UserDto, errors: Errors): ResponseEntity[UserDto] = {
    if (errors.hasErrors) {
      throw new DataNotValidatedException(new Error("The user has not been validated"))
    }

    new ResponseEntity(userService.addUser(user), HttpStatus.OK)
  }

  @PostMapping(Array("/login"))
  @throws[Exception]
  def createAuthenticationToken(@RequestBody authenticationRequest: AuthenticationRequest, errors: Errors): ResponseEntity[UserDto] = {
    val authOptional = userService.authenticateUser(authenticationRequest.getUsername, authenticationRequest.getPassword)
    if (authOptional.isEmpty) throw new CredentialNotFoundException("Invalid username or password!")
    val userDto = userService.getUserByEmail(authenticationRequest.getUsername)
    val userDetails = tempConverter.userDtoToEntity(userDto)
    val jwt = jwtTokenUtil.generateToken(userDetails)
    val authUser = tempConverter.userEntityToDto(userDetails)
    authUser.setAuthToken(jwt)
    new ResponseEntity(authUser, HttpStatus.OK)
  }

  @PreAuthorize(value = "hasAuthority('ADMIN')")
  @GetMapping(Array("/allUsers"))
  def getAllUsers: ResponseEntity[Array[UserDto]] = {
    new ResponseEntity(userService.listAllUsers, HttpStatus.OK)
  }

  @GetMapping(Array("/currentUser"))
  def getCurrentUser: ResponseEntity[UserDto] = {
    new ResponseEntity(userService.getCurrentUser, HttpStatus.OK)
  }

  @PreAuthorize(value = "hasAuthority('ADMIN')")
  @GetMapping(Array("/userDetails/{userId}"))
  def getUserDetails(@PathVariable("userId") userId: Integer): ResponseEntity[UserDto] = {
    new ResponseEntity(userService.getUserById(userId), HttpStatus.OK)
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @DeleteMapping(Array("/deleteUser/{userId}"))
  def deleteUser(@PathVariable userId: Integer,
                 @RequestHeader(value = "X-Source-Service", required = false) sourceService: String): ResponseEntity[String] = {
    if (sourceService != "customer-service") {
      return new ResponseEntity[String](HttpStatus.FORBIDDEN) // 403 Forbidden
    }

    userService.deleteUser(userId)
    new ResponseEntity(s"The user with id $userId has been removed!", HttpStatus.OK)
  }

  @GetMapping(Array("/userData/{username}"))
  def getUserDetails(
                      @PathVariable("username") username: String,
                      @RequestHeader(value = "X-Source-Service", required = false) sourceService: String
                    ): ResponseEntity[UserDto] = {
    if (!serviceList.contains(sourceService)) {
      new ResponseEntity(HttpStatus.FORBIDDEN)
    } else {
      new ResponseEntity(userService.getUserByEmail(username), HttpStatus.OK)
    }
  }

  @GetMapping(Array("/roles/{userId}"))
  def getUserRoles(@PathVariable("userId") userId: Integer,
                   @RequestHeader(value = "X-Source-Service", required = false) sourceService: String): ResponseEntity[Array[RoleDto]] = {
    if (!serviceList.contains(sourceService)) {
      new ResponseEntity(HttpStatus.FORBIDDEN)
    }

    new ResponseEntity(roleService.listAllByUserId(userId), HttpStatus.OK)
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @PutMapping(Array("/suspendUser/{userId}"))
  def suspendUser(
                   @RequestHeader(value = "X-Source-Service", required = false) sourceService: String,
                   @PathVariable("userId") userId: Int
                 ): ResponseEntity[Void] = {
    if (sourceService != "customer-service") {
      new ResponseEntity(HttpStatus.FORBIDDEN)
    }
    userService.suspendUser(userId)
    new ResponseEntity(HttpStatus.OK)
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @PutMapping(Array("/reactivateUser/{userId}"))
  def reactivateUser(
                      @RequestHeader(value = "X-Source-Service", required = false) sourceService: String,
                      @PathVariable("userId") userId: Int
                    ): ResponseEntity[Void] = {
    if (sourceService != "customer-service") {
      new ResponseEntity(HttpStatus.FORBIDDEN)
    }
    userService.clearSuspension(userId)
    new ResponseEntity(HttpStatus.OK)
  }

}
