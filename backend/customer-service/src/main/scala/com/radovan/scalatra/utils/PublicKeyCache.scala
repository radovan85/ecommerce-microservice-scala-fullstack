package com.radovan.scalatra.utils

import com.github.benmanes.caffeine.cache.{Cache, Caffeine}
import jakarta.inject.{Inject, Singleton}
import org.slf4j.{Logger, LoggerFactory}
import com.radovan.scalatra.config.ApacheHttpClientSync

import java.security.{KeyFactory, PublicKey}
import java.security.spec.X509EncodedKeySpec
import java.util.Base64
import java.util.concurrent.{CompletableFuture, TimeUnit}

@Singleton
class PublicKeyCache @Inject() (
                                 urlProvider: ServiceUrlProvider
                               ) {

  private val logger: Logger = LoggerFactory.getLogger(classOf[PublicKeyCache])
  private val CACHE_KEY = "jwt-public-key"

  private val cache: Cache[String, PublicKey] = Caffeine.newBuilder()
    .expireAfterWrite(12, TimeUnit.HOURS)
    .build()

  def getPublicKey(): CompletableFuture[PublicKey] = {
    CompletableFuture.supplyAsync(() => {
      val cached = cache.getIfPresent(CACHE_KEY)
      if (cached != null) cached
      else refreshPublicKey()
    })
  }

  def refreshPublicKey(): PublicKey = {
    try {
      val newKey = fetchAndParsePublicKey()
      cache.put(CACHE_KEY, newKey)
      newKey
    } catch {
      case e: Exception =>
        logger.error("Failed to refresh public key", e)
        throw new RuntimeException("Public key refresh failed", e)
    }
  }

  private def fetchAndParsePublicKey(): PublicKey = {
    val pem = fetchPublicKeyFromAuthService()
    parsePublicKey(cleanKey(pem))
  }

  private def fetchPublicKeyFromAuthService(): String = {
    val url = s"${urlProvider.getAuthServiceUrl}/api/auth/public-key"
    logger.debug(s"Fetching public key from: $url")

    val (status, body) = ApacheHttpClientSync.get(url)

    if (status != 200) {
      throw new RuntimeException(s"HTTP $status")
    }

    body
  }

  private def cleanKey(pem: String): String = {
    pem.replace("-----BEGIN PUBLIC KEY-----", "")
      .replace("-----END PUBLIC KEY-----", "")
      .replaceAll("\\s+", "")
  }

  private def parsePublicKey(base64: String): PublicKey = {
    val decoded = Base64.getDecoder.decode(base64)
    val spec = new X509EncodedKeySpec(decoded)
    KeyFactory.getInstance("RSA").generatePublic(spec)
  }

  def isKeyAvailable: Boolean = cache.getIfPresent(CACHE_KEY) != null
}

