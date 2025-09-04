package com.radovan.scalatra.security

import com.radovan.scalatra.utils.{JwtUtil, ServiceUrlProvider}
import com.radovan.scalatra.config.ApacheHttpClientSync
import com.radovan.scalatra.exceptions.ForbiddenAccessException
import jakarta.inject.Inject
import org.scalatra.ScalatraBase
import org.slf4j.LoggerFactory

import scala.jdk.CollectionConverters._
import java.util.concurrent.CompletableFuture
import java.util.function.BiFunction

trait SecuritySupport extends ScalatraBase {

  @Inject var jwtUtil: JwtUtil = _
  @Inject var urlProvider: ServiceUrlProvider = _
  private val logger = LoggerFactory.getLogger(getClass)

  // 🔒 JWT autentifikacija — izvršava se pre svake rute
  before() {
    val exemptPaths = Set("/api/customers/register")
    val path = request.getRequestURI
    val method = request.getMethod
    val authHeader = Option(request.getHeader("Authorization"))

    val isExempt = exemptPaths.contains(path)

    if (isExempt && authHeader.isEmpty) {
      logger.info(s"🔓 Public endpoint hit: $method $path — skipping auth")
    } else if (isExempt && authHeader.nonEmpty) {
      logger.warn(s"🚫 Authenticated user attempted to access public endpoint: $method $path")
      halt(403, "Authenticated users cannot access this endpoint")
    } else {
      authHeader match {
        case Some(header) if header.startsWith("Bearer ") =>
          val token = header.substring(7)

          val usernameF: CompletableFuture[Option[String]] = jwtUtil.extractUsername(token)
          val rolesF: CompletableFuture[Option[List[String]]] = jwtUtil.extractRoles(token)

          val combinedF: CompletableFuture[(Option[String], Option[List[String]])] =
            usernameF.thenCombine(rolesF, new BiFunction[Option[String], Option[List[String]], (Option[String], Option[List[String]])] {
              override def apply(userOpt: Option[String], rolesOpt: Option[List[String]]) =
                (userOpt, rolesOpt)
            })

          val result = combinedF.get()

          result match {
            case (Some(userId), Some(rawRoles)) =>
              val roles = rawRoles.asInstanceOf[List[String]]
              if (roles.nonEmpty) {
                request.setAttribute("userId", userId)
                request.setAttribute("roles", roles.toSet)

                // 🚨 Antifraud 451 check for ROLE_USER
                if (roles.contains("ROLE_USER")) {
                  val statusUrl = s"${urlProvider.getAuthServiceUrl}/api/auth/me"
                  val (statusCode, body) = ApacheHttpClientSync.get(statusUrl, Map("Authorization" -> s"Bearer $token"))

                  if (statusCode == 451) {
                    throw new ForbiddenAccessException(s"Suspended user $userId attempted access to $path")
                  }

                }

              } else {
                halt(401, "Token roles are malformed")
              }

            case _ =>
              halt(401, "Invalid or expired token")
          }

        case _ =>
          halt(401, "Missing Authorization header")
      }
    }
  }

  // ✅ Role-check: mora imati makar jednu od traženih rola
  def secured(requiredRoles: Set[String])(block: => Any): Any = {
    val userRoles = getRoles
    if (requiredRoles.exists(userRoles.contains)) block
    else halt(403, "Access denied: insufficient role")
  }

  // ✅ Role-check: bilo koja rola (autentifikovan korisnik)
  def secured(block: => Any): Any = {
    val userRoles = getRoles
    if (userRoles.nonEmpty) block
    else halt(403, "Access denied: no roles assigned")
  }

  // 🆔 Helper: uzmi userId iz requesta
  def getUserId: Option[String] =
    Option(request.getAttribute("userId")).map(_.toString)

  def getRoles: Set[String] =
    Option(request.getAttribute("roles")) match {
      case Some(set: Set[_]) =>
        set.collect { case s: String => s }
      case Some(raw: java.util.Set[_]) =>
        raw.asScala.collect { case s: String => s }.toSet
      case _ =>
        Set.empty[String]
    }

  // 🔍 Helper: da li korisnik ima određenu rolu
  def hasRole(role: String): Boolean = getRoles.contains(role)
}
