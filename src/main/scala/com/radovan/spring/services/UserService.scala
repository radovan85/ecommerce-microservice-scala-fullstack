package com.radovan.spring.services

import com.radovan.spring.dto.UserDto
import org.springframework.security.core.Authentication

trait UserService {

  def getUserById(id:Integer):UserDto

  def listAllUsers:Array[UserDto]

  def getUserByEmail(email:String):UserDto

  def getCurrentUser:UserDto

  def suspendUser(userId:Integer):Unit

  def clearSuspension(userId:Integer):Unit

  def isAdmin:Boolean

  def authenticateUser(username: String, password: String): Option[Authentication]

  def addUser(user:UserDto):UserDto

  def deleteUser(userId:Integer):Unit
}
