package com.radovan.play.security

import com.radovan.play.utils.{JwtUtil, ServiceUrlProvider}
import com.radovan.play.exceptions.ForbiddenAccessException
import jakarta.inject.Inject
import play.api.libs.ws.WSClient
import play.api.mvc.{ActionBuilder, AnyContent, BodyParser, BodyParsers, Request, Result, Results}

import scala.concurrent.{ExecutionContext, Future}

class JwtSecuredAction @Inject()(
                                  parser: BodyParsers.Default,
                                  jwtUtil: JwtUtil,
                                  ws: WSClient,
                                  serviceUrlProvider: ServiceUrlProvider
                                )(implicit ec: ExecutionContext)
  extends ActionBuilder[SecuredRequest, AnyContent] {

  override def parser: BodyParser[AnyContent] = parser
  override protected def executionContext: ExecutionContext = ec

  override def invokeBlock[A](request: Request[A], block: SecuredRequest[A] => Future[Result]): Future[Result] = {
    val exemptPaths = Set("/api/health")
    val path = request.uri
    val method = request.method
    val authHeader = request.headers.get("Authorization")

    val isExempt = exemptPaths.contains(path)

    if (isExempt && authHeader.isEmpty) {
      block(SecuredRequest("anonymous", Set.empty, request))
    } else if (isExempt && authHeader.nonEmpty) {
      Future.successful(Results.Forbidden("Authenticated users cannot access this endpoint"))
    } else {
      authHeader match {
        case Some(header) if header.startsWith("Bearer ") =>
          val token = header.drop(7)

          for {
            usernameOpt <- jwtUtil.extractUsername(token)
            rolesOpt    <- jwtUtil.extractRoles(token)
            result <- (usernameOpt, rolesOpt) match {
              case (Some(userId), Some(roleList)) if roleList.nonEmpty =>
                val roles = roleList.toSet
                val authUrl = serviceUrlProvider.getAuthServiceUrl + "/api/auth/me"

                ws.url(authUrl)
                  .addHttpHeaders("Authorization" -> s"Bearer $token")
                  .get()
                  .flatMap { response =>
                    response.status match {
                      case 200 =>
                        block(SecuredRequest(userId, roles, request))
                      case 451 =>
                        Future.failed(new ForbiddenAccessException("User access suspended"))
                      case _ =>
                        Future.successful(Results.InternalServerError("Unexpected response from auth service"))
                    }
                  }

              case _ =>
                Future.successful(Results.Unauthorized("Invalid or expired token"))
            }
          } yield result

        case _ =>
          Future.successful(Results.Unauthorized("Missing Authorization header"))
      }
    }
  }
}
