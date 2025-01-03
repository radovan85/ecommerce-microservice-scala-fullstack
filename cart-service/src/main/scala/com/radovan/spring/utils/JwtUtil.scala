package com.radovan.spring.utils

import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.{Claims, Jwts}
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service

import java.util
import java.util.Date
import java.util.function.Function
import javax.crypto.SecretKey

@Service
class JwtUtil {

  private val SECRET_KEY = "71003c530442e348052a20bd7b10135752708873b3584a719fa67f62ca2c63ca"

  def extractUsername(token: String): String = {
    val claims = extractAllClaims(token)
    claims.getSubject
  }

  def extractExpiration(token: String): Date = {
    val claims = extractAllClaims(token)
    claims.getExpiration
  }

  def extractClaim[T](token: String, claimsResolver: Function[Claims, T]): T = {
    val claims = extractAllClaims(token)
    claimsResolver.apply(claims)
  }

  private def extractAllClaims(token: String): Claims = {
    Jwts.parser()
      .setSigningKey(getSignInKey())
      .build()
      .parseClaimsJws(token)
      .getBody
  }

  private def getSignInKey(): SecretKey = {
    val keyBytes = Decoders.BASE64.decode(SECRET_KEY)
    Keys.hmacShaKeyFor(keyBytes)
  }

  private def isTokenExpired(token: String): Boolean = {
    extractExpiration(token).before(new Date())
  }

  def generateToken(userDetails: UserDetails): String = {
    val claims = new util.HashMap[String, Object]()
    createToken(claims, userDetails.getUsername)
  }

  def createToken(claims: util.Map[String, Object], subject: String): String = {
    Jwts.builder()
      .setClaims(claims)
      .setSubject(subject)
      .setIssuedAt(new Date())
      .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
      .signWith(getSignInKey())
      .compact()
  }

  def validateToken(token: String, username: String): Boolean = {
    val tokenUsername = extractUsername(token)
    tokenUsername.equals(username) && !isTokenExpired(token)
  }
}
