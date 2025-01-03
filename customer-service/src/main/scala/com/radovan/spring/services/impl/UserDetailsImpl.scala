package com.radovan.spring.services.impl

import java.util
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.{UserDetails, UserDetailsService}
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import com.fasterxml.jackson.databind.JsonNode
import com.radovan.spring.utils.{CustomUserDetails, ServiceUrlProvider}
import flexjson.JSONDeserializer

import scala.collection.mutable.ArrayBuffer
import scala.jdk.CollectionConverters._

@Service
class UserDetailsImpl extends UserDetailsService {

  private var urlProvider:ServiceUrlProvider = _
  private var restTemplate:RestTemplate = _

  @Autowired
  private def initialize(urlProvider: ServiceUrlProvider,restTemplate: RestTemplate):Unit = {
    this.urlProvider = urlProvider
    this.restTemplate = restTemplate
  }

  override def loadUserByUsername(username: String): UserDetails = {
    val userUrl = s"${urlProvider.getUserServiceUrl}/userData/$username"

    val userResponse: JsonNode = restTemplate.getForObject(userUrl, classOf[JsonNode])

    val userJsonString = userResponse.toString

    val deserializer = new JSONDeserializer[util.Map[String, Any]]()

    val userMap: util.Map[String, Any] = deserializer.deserialize(userJsonString)

    val id = userMap.get("id").toString
    val email = userMap.get("email").toString
    val enabled = userMap.get("enabled").toString

    val rolesUrl = s"${urlProvider.getUserServiceUrl}/roles/$id"
    val response: ResponseEntity[JsonNode] = restTemplate.getForEntity(rolesUrl, classOf[JsonNode])

    val authorities = new ArrayBuffer[GrantedAuthority]()
    val rolesList = response.getBody.get("item")
    if (rolesList != null) {
      val roleName = rolesList.get("role").asText
      val authority: GrantedAuthority = new SimpleGrantedAuthority(roleName)
      authorities += authority
    } else {
      println("No roles found for this user.")
    }

    val customUserDetails = new CustomUserDetails
    customUserDetails.setEmail(email)
    customUserDetails.setEnabled(enabled.toByte)
    customUserDetails.setAuthorities(authorities.asJava)

    customUserDetails
  }
}

