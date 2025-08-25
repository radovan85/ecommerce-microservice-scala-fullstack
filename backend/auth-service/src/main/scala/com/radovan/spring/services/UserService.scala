package com.radovan.spring.services

import com.radovan.spring.dto.UserDto
import org.springframework.security.core.Authentication

trait UserService {

  def listAll:Array[UserDto]
  def getCurrentUser:UserDto
  def getUserById(userId:Integer):UserDto
  def getUserByEmail(email:String):UserDto
  def authenticateUser(username:String,password:String):Option[Authentication]
  def isAdmin(userId:Integer):Boolean
  def suspendUser(userId:Integer):Unit
  def reactivateUser(userId:Integer):Unit
  def addUser(user:UserDto):UserDto
  def deleteUser(userId:Integer):Unit
}
