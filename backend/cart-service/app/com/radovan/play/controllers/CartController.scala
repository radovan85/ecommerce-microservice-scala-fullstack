package com.radovan.play.controllers

import com.radovan.play.brokers.CartNatsSender
import com.radovan.play.utils.TokenUtils._
import com.radovan.play.dto.{CartDto, CartItemDto}
import com.radovan.play.services.{CartItemService, CartService}
import com.radovan.play.utils.{NodeUtils, ResponsePackage}
import com.radovan.play.security.{JwtSecuredAction, SecuredRequest}
import jakarta.inject.Inject
import org.apache.hc.core5.http.HttpStatus
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents, Result}

class CartController @Inject()(
                                cc: ControllerComponents,
                                cartService: CartService,
                                cartItemService: CartItemService,
                                natsSender: CartNatsSender,
                                securedAction: JwtSecuredAction
                              ) extends AbstractController(cc) {

  private def onlyUser[A](secured: SecuredRequest[A])(block: => Result): Result = {
    if (secured.roles.contains("ROLE_USER")) block
    else Forbidden("Access denied")
  }

  private def extractCartId[A](secured: SecuredRequest[A]): Int = {
    val customerNode = natsSender.retrieveCurrentCustomer(provideToken(secured))
    customerNode.get("cartId").asInt()
  }

  def getMyItems: Action[AnyContent] = securedAction { secured =>
    onlyUser(secured) {
      val cartId = extractCartId(secured)
      val items = cartItemService.listAllByCartId(cartId)
      new ResponsePackage[Array[CartItemDto]](items, HttpStatus.SC_OK).toResult
    }
  }

  def addCartItem(productId: Int): Action[AnyContent] = securedAction { secured =>
    onlyUser(secured) {
      cartItemService.addCartItem(productId, provideToken(secured))
      new ResponsePackage[String]("The item has been added to the cart!", HttpStatus.SC_OK).toResult
    }
  }

  def deleteItem(itemId: Int): Action[AnyContent] = securedAction { secured =>
    onlyUser(secured) {
      cartItemService.removeCartItem(itemId, provideToken(secured))
      new ResponsePackage[String]("The item has been removed from the cart!", HttpStatus.SC_OK).toResult
    }
  }

  def clearCart: Action[AnyContent] = securedAction { secured =>
    onlyUser(secured) {
      cartService.clearCart(provideToken(secured))
      new ResponsePackage[String]("Your cart is clear!", HttpStatus.SC_OK).toResult
    }
  }

  def getMyCart: Action[AnyContent] = securedAction { secured =>
    onlyUser(secured) {
      val cartId = extractCartId(secured)
      new ResponsePackage[CartDto](cartService.getCartById(cartId), HttpStatus.SC_OK).toResult
    }
  }

  def validateCart: Action[AnyContent] = securedAction { secured =>
    onlyUser(secured) {
      val cartId = extractCartId(secured)
      new ResponsePackage[CartDto](cartService.validateCart(cartId), HttpStatus.SC_OK).toResult
    }
  }
}
