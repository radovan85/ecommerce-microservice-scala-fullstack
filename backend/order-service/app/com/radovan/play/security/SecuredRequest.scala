package com.radovan.play.security

import play.api.mvc.{Request, WrappedRequest}

case class SecuredRequest[A](
                              userId: String,
                              roles: Set[String],
                              request: Request[A]
                            ) extends WrappedRequest[A](request)

