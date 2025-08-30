package com.radovan.scalatra.utils

import io.jsonwebtoken.{Claims, Jwts}
import jakarta.inject.{Inject, Provider, Singleton}
import org.slf4j.{Logger, LoggerFactory}

import java.util.{Date, Optional => JOptional}
import java.util.concurrent.CompletableFuture
import java.util.function.Function
import scala.jdk.CollectionConverters._
import scala.jdk.FutureConverters._

@Singleton
class JwtUtil @Inject() (private val publicKeyCacheProvider: Provider[PublicKeyCache]) {

  private val logger: Logger = LoggerFactory.getLogger(classOf[JwtUtil])

  def validateToken(token: String): CompletableFuture[Boolean] = {
    publicKeyCacheProvider.get().getPublicKey().thenApply { publicKey =>
      try {
        Jwts.parser()
          .verifyWith(publicKey)
          .build()
          .parseSignedClaims(token)
        true
      } catch {
        case e: Exception =>
          logger.warn("Token validation failed: {}", e.getMessage)
          false
      }
    }
  }

  def extractUsername(token: String): CompletableFuture[Option[String]] = {
    extractClaim(token, (claims: Claims) => claims.getSubject)
      .exceptionally { ex =>
        logger.error("Username extraction failed", ex)
        None
      }
  }

  def extractRoles(token: String): CompletableFuture[Option[List[String]]] = {
    extractClaim(token, (claims: Claims) => {
      val rawRoles = claims.get("roles")
      rawRoles match {
        case list: java.util.List[_] =>
          val roles = list.asScala.collect { case s: String => s }.toList
          roles
        case _ => List.empty[String]
      }
    })
  }

  def extractExpiration(token: String): CompletableFuture[Option[Date]] = {
    extractClaim(token, (claims: Claims) => claims.getExpiration)
  }






  private def extractClaim[T](token: String, resolver: Function[Claims, T]): CompletableFuture[Option[T]] = {
    publicKeyCacheProvider.get().getPublicKey().thenApply { publicKey =>
      try {
        val claims = Jwts.parser()
          .verifyWith(publicKey)
          .build()
          .parseSignedClaims(token)
          .getPayload

        Option(resolver.apply(claims))
      } catch {
        case e: Exception =>
          logger.warn("Token parsing failed", e)
          None
      }
    }
  }





  def cleanToken(token: String): String = {
    if (token != null && token.startsWith("Bearer ")) token.substring(7)
    else token
  }
}
