package com.radovan.spring.utils

import java.util
import io.jsonwebtoken.{Claims, Jwts}
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.{Date, HashMap, Map}
import java.util.function.Function
import javax.crypto.SecretKey

@Service
class JwtUtil {

  private val SECRET_KEY: String = "71003c530442e348052a20bd7b10135752708873b3584a719fa67f62ca2c63ca"

  def extractUsername(token: String): String =
    extractClaim(token, _.getSubject)

  def extractExpiration(token: String): Date =
    extractClaim(token, _.getExpiration)

  def extractClaim[T](token: String, claimsResolver: Function[Claims, T]): T = {
    val claims: Claims = extractAllClaims(token)
    claimsResolver.apply(claims)
  }

  private def extractAllClaims(token: String): Claims =
    Jwts.parser()
      .verifyWith(getSignInKey)
      .build()
      .parseSignedClaims(token)
      .getPayload

  private def getSignInKey: SecretKey = {
    val keyBytes: Array[Byte] = Decoders.BASE64.decode(SECRET_KEY)
    Keys.hmacShaKeyFor(keyBytes)
  }

  private def isTokenExpired(token: String): Boolean =
    extractExpiration(token).before(new Date())

  def generateToken(userDetails: UserDetails): String = {
    val claims: util.Map[String, Object] = new util.HashMap()
    createToken(claims, userDetails.getUsername)
  }

  def createToken(claims: util.Map[String, Object], subject: String): String =
    Jwts.builder()
      .claims(claims)
      .subject(subject)
      .issuedAt(new Date())
      .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
      .signWith(getSignInKey)
      .compact()

  def validateToken(token: String, userDetails: UserDetails): Boolean = {
    val username: String = extractUsername(token)
    username.equals(userDetails.getUsername) && !isTokenExpired(token)
  }
}