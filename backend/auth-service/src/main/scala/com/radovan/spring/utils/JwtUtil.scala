package com.radovan.spring.utils

import io.jsonwebtoken.{Claims, Jwts}
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.PropertySource
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

import java.security.{KeyFactory, PrivateKey, PublicKey}
import java.security.spec.{PKCS8EncodedKeySpec, X509EncodedKeySpec}
import java.time.Instant
import java.util.{Base64, Date}
import scala.jdk.CollectionConverters._
import scala.util.{Failure, Success, Try}

@Component
@PropertySource(Array("classpath:application.properties"))
class JwtUtil @Autowired()(private val environment: Environment) {

  private val logger: Logger = LoggerFactory.getLogger(classOf[JwtUtil])

  private var privateKey: PrivateKey = _
  private var publicKey: PublicKey = _
  private var jwtExpiration: Long = _

  @Autowired
  def init(): Unit = {
    val privateKeyString = Option(environment.getProperty("jwt.private-key"))
    val publicKeyString  = Option(environment.getProperty("jwt.public-key"))
    val expiration       = Option(environment.getProperty("jwt.expiration"))

    if (privateKeyString.isEmpty || publicKeyString.isEmpty || expiration.isEmpty) {
      throw new IllegalStateException("JWT configuration missing in application.properties")
    }

    this.privateKey = loadPrivateKey(cleanKeyString(privateKeyString.get))
    this.publicKey  = loadPublicKey(cleanKeyString(publicKeyString.get))
    this.jwtExpiration = expiration.get.toLong
  }

  private def cleanKeyString(keyString: String): String = {
    if (keyString == null) throw new IllegalArgumentException("Key string cannot be null")
    keyString
      .replaceAll("-----(BEGIN|END) (PRIVATE|PUBLIC) KEY-----", "")
      .replaceAll("\\s", "")
  }

  private def loadPrivateKey(keyString: String): PrivateKey = {
    Try {
      val keyBytes = Base64.getDecoder.decode(keyString.trim)
      val spec     = new PKCS8EncodedKeySpec(keyBytes)
      val kf       = KeyFactory.getInstance("RSA")
      kf.generatePrivate(spec)
    } match {
      case Success(pk) => pk
      case Failure(e)  => throw new RuntimeException(s"Failed to load private key. Key: $keyString", e)
    }
  }

  private def loadPublicKey(keyString: String): PublicKey = {
    Try {
      val keyBytes = Base64.getDecoder.decode(keyString.trim)
      val spec     = new X509EncodedKeySpec(keyBytes)
      val kf       = KeyFactory.getInstance("RSA")
      kf.generatePublic(spec)
    } match {
      case Success(pk) => pk
      case Failure(e) =>
        logger.error("Failed to load public key", e)
        throw new RuntimeException("Failed to load public key", e)
    }
  }

  def generateToken(username: String, roles: List[String]): String = {
    val claims = Map[String, AnyRef](
      "username" -> username,
      "roles"    -> roles.asJava
    ).asJava

    Jwts.builder()
      .claims(claims)
      .subject(username)
      .issuedAt(Date.from(Instant.now()))
      .expiration(Date.from(Instant.now().plusSeconds(jwtExpiration)))
      .signWith(privateKey, Jwts.SIG.RS256)
      .compact()
  }

  def validateToken(token: String): Boolean = {
    Try {
      Jwts.parser().verifyWith(publicKey).build().parseSignedClaims(token)
    } match {
      case Success(_) => true
      case Failure(e) =>
        logger.warn(s"Invalid JWT token: ${e.getMessage}")
        false
    }
  }

  def extractUsername(token: String): String =
    extractClaim(token, _.getSubject)

  def extractRoles(token: String): List[String] =
    extractClaim(token, claims => claims.get("roles", classOf[java.util.List[String]]).asScala.toList)

  def extractExpiration(token: String): Date =
    extractClaim(token, _.getExpiration)

  def extractClaim[T](token: String, claimsResolver: Claims => T): T = {
    val claims = extractAllClaims(token)
    claimsResolver(claims)
  }

  def extractAllClaims(token: String): Claims = {
    Jwts.parser().verifyWith(publicKey).build().parseSignedClaims(token).getPayload
  }

  def isTokenExpired(token: String): Boolean =
    extractExpiration(token).before(new Date())

  def getPublicKey: PublicKey = publicKey

  def getPublicKeyAsPEM: String = {
    Try {
      val encoded = Base64.getEncoder.encodeToString(publicKey.getEncoded)
      s"-----BEGIN PUBLIC KEY-----\n${splitKeyIntoLines(encoded)}\n-----END PUBLIC KEY-----"
    } match {
      case Success(pem) => pem
      case Failure(e) =>
        logger.error("Failed to export public key", e)
        throw new RuntimeException("Failed to export public key", e)
    }
  }

  def getPublicKeyAsSingleLine: String = {
    Try {
      Base64.getEncoder.encodeToString(publicKey.getEncoded)
    } match {
      case Success(key) => key
      case Failure(e) =>
        logger.error("Failed to export public key", e)
        throw new RuntimeException("Failed to export public key", e)
    }
  }

  private def splitKeyIntoLines(key: String): String =
    key.replaceAll("(.{64})", "$1\n").trim
}
