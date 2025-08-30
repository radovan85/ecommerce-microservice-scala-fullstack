package com.radovan.play.utils

import io.jsonwebtoken.{Claims, Jwts}
import jakarta.inject.{Inject, Provider, Singleton}
import org.slf4j.LoggerFactory

import java.util.{Date, Optional => JOptional}
import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters._
import scala.util.{Failure, Success, Try}

@Singleton
class JwtUtil @Inject() (
                          publicKeyCacheProvider: Provider[PublicKeyCache]
                        )(implicit ec: ExecutionContext) {

  private val logger = LoggerFactory.getLogger(getClass)

  def validateToken(token: String): Future[Boolean] =
    publicKeyCacheProvider.get().getPublicKey.map { publicKey =>
      Try {
        Jwts.parser()
          .verifyWith(publicKey)
          .build()
          .parseSignedClaims(token)
        true
      }.getOrElse {
        logger.warn("Token validation failed")
        false
      }
    }

  def extractUsername(token: String): Future[Option[String]] =
    extractClaim(token, _.getSubject)


  def extractRoles(token: String): Future[Option[List[String]]] =
    extractClaim(token, claims => {
      Option(claims.get("roles")) match {
        case Some(list: java.util.List[_]) =>
          list.asScala.collect { case s: String => s }.toList
        case _ => List.empty[String]
      }
    })



  /*
  def extractRoles(token: String): Future[Option[List[String]]] =
    extractClaim[List[String]](token, claims => {
      val rawRoles = claims.get("roles")
      println(s"🔍 Raw roles claim: $rawRoles (${Option(rawRoles).map(_.getClass.getName).getOrElse("null")})")

      val parsedRoles = rawRoles match {
        case list: java.util.List[_] =>
          println(s"✅ Roles is a java.util.List with ${list.size()} elements")
          list.asScala.collect { case s: String => s }.toList

        case s: String =>
          println(s"⚠️ Roles is a String: $s — wrapping in List")
          List(s)

        case null =>
          println("❌ Roles claim is null")
          List.empty[String]

        case other =>
          println(s"⚠️ Roles is unexpected type: ${other.getClass.getName}")
          List.empty[String]
      }

      parsedRoles
    })

   */



  def extractExpiration(token: String): Future[Option[Date]] =
    extractClaim(token, _.getExpiration)

  /*
  private def extractClaim[T](token: String, resolver: Claims => T): Future[Option[T]] =
    publicKeyCacheProvider.get().getPublicKey.map { publicKey =>
      Try {
        val claims = Jwts.parser()
          .verifyWith(publicKey)
          .build()
          .parseSignedClaims(token)
          .getPayload

        Option(resolver(claims))
      }.recover {
        case e =>
          logger.warn("Token parsing failed", e)
          None
      }.get
    }

   */

  private def extractClaim[T](token: String, resolver: Claims => T): Future[Option[T]] =
    publicKeyCacheProvider.get().getPublicKey.map { publicKey =>
      Try {
        val claims = Jwts.parser()
          .verifyWith(publicKey)
          .build()
          .parseSignedClaims(token)
          .getPayload

        Option(resolver(claims))
      }.recover {
        case e =>
          logger.warn("Token parsing failed", e)
          None
      }.get
    }

  def cleanToken(token: String): String =
    if (token != null && token.startsWith("Bearer ")) token.substring(7)
    else token
}

