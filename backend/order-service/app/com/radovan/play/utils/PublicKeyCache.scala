package com.radovan.play.utils

import com.github.benmanes.caffeine.cache.{Cache, Caffeine}
import jakarta.inject.{Inject, Singleton}
import org.slf4j.LoggerFactory
import play.libs.ws.WSClient

import java.security.spec.X509EncodedKeySpec
import java.security.{KeyFactory, PublicKey}
import java.util.Base64
import java.util.concurrent.TimeUnit
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

@Singleton
class PublicKeyCache @Inject() (
                                 wsClient: WSClient,
                                 urlProvider: ServiceUrlProvider
                               )(implicit ec: ExecutionContext) {

  private val logger = LoggerFactory.getLogger(getClass)
  private val CACHE_KEY = "jwt-public-key"

  private val cache: Cache[String, PublicKey] =
    Caffeine.newBuilder()
      .expireAfterWrite(12, TimeUnit.HOURS)
      .build()

  def getPublicKey: Future[PublicKey] = Future {
    Option(cache.getIfPresent(CACHE_KEY)) match {
      case Some(key) => key
      case None      => refreshPublicKey()
    }
  }

  def refreshPublicKey(): PublicKey = {
    Try(fetchAndParsePublicKey()) match {
      case Success(newKey) =>
        cache.put(CACHE_KEY, newKey)
        newKey
      case Failure(e) =>
        logger.error("Failed to refresh public key", e)
        throw new RuntimeException("Public key refresh failed", e)
    }
  }

  private def fetchAndParsePublicKey(): PublicKey = {
    val publicKeyPem = fetchPublicKeyFromAuthService()
    parsePublicKey(cleanKey(publicKeyPem))
  }

  private def fetchPublicKeyFromAuthService(): String = {
    val url = s"${urlProvider.getAuthServiceUrl}/api/auth/public-key"
    logger.debug(s"Fetching public key from: $url")

    val response = wsClient.url(url)
      .setRequestTimeout(java.time.Duration.ofMillis(5000))
      .get()
      .toCompletableFuture
      .join()


    if (response.getStatus != 200)
      throw new RuntimeException(s"HTTP ${response.getStatus}")

    response.getBody
  }

  private def cleanKey(publicKeyPem: String): String =
    publicKeyPem
      .replace("-----BEGIN PUBLIC KEY-----", "")
      .replace("-----END PUBLIC KEY-----", "")
      .replaceAll("\\s+", "")

  private def parsePublicKey(keyBase64: String): PublicKey = {
    val decodedKey = Base64.getDecoder.decode(keyBase64)
    val keySpec = new X509EncodedKeySpec(decodedKey)
    KeyFactory.getInstance("RSA").generatePublic(keySpec)
  }

  def isKeyAvailable: Boolean =
    cache.getIfPresent(CACHE_KEY) != null
}

