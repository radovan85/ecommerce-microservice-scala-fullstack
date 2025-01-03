package com.radovan.spring.utils

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

import java.util
import scala.beans.BeanProperty

@SerialVersionUID(1L)
class CustomUserDetails extends UserDetails {

  @BeanProperty var email:String = _
  @BeanProperty var password:String = _
  @BeanProperty var enabled:Byte = _
  @BeanProperty var authorities: util.Collection[GrantedAuthority] = _

  override def getUsername: String = password

  override def isAccountNonExpired: Boolean = true

  override def isAccountNonLocked: Boolean = true

  override def isCredentialsNonExpired: Boolean = true

  override def isEnabled: Boolean = true
}
