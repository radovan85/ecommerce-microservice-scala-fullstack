package com.radovan.play.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.radovan.play.brokers.OrderNatsSender
import com.radovan.play.utils.TokenUtils._
import com.radovan.play.security.{JwtSecuredAction, SecuredRequest}
import com.radovan.play.services.{OrderAddressService, OrderItemService, OrderService}
import com.radovan.play.utils.ResponsePackage
import jakarta.inject.Inject
import org.apache.hc.core5.http.HttpStatus
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents, Result}

class OrderController @Inject()(
                                 cc: ControllerComponents,
                                 objectMapper:ObjectMapper,
                                 orderService: OrderService,
                                 securedAction: JwtSecuredAction,
                                 itemService: OrderItemService,
                                 addressService: OrderAddressService,
                                 natsSender: OrderNatsSender
                               ) extends AbstractController(cc) {

  private def onlyAdmin[A](secured: SecuredRequest[A])(block: => Result): Result = {
    if (secured.roles.contains("ROLE_ADMIN")) block
    else Forbidden("Access denied: admin role required")
  }

  private def onlyUser[A](secured: SecuredRequest[A])(block: => Result): Result = {
    if (secured.roles.contains("ROLE_USER")) block
    else Forbidden("Access denied: user role required")
  }

  def provideMyAddress: Action[AnyContent] = securedAction { secured =>
    onlyUser(secured){
      val customerNode = natsSender.retrieveCurrentCustomer(provideToken(secured))
      val addressId = customerNode.get("shippingAddressId").asInt()
      val addressNode = natsSender.retrieveAddress(addressId,provideToken(secured))
      println(s"Address node:   ${addressNode}")
      new ResponsePackage(addressNode, HttpStatus.SC_OK).toResult
    }
  }

  def confirmShipping: Action[AnyContent] = securedAction { secured =>
    onlyUser(secured){
      val json = Json.stringify(secured.body.asJson.getOrElse(Json.obj()))
      val customerNode = natsSender.retrieveCurrentCustomer(provideToken(secured))
      val addressId = customerNode.get("shippingAddressId").asInt()

      val addressNode = objectMapper.readTree(json)
      natsSender.updateShippingAddress(addressNode,addressId,provideToken(secured))
      new ResponsePackage("Your address has been updated!",HttpStatus.SC_OK).toResult
    }
  }


  def getAllOrders: Action[AnyContent] = securedAction { secured =>
    onlyAdmin(secured){
      new ResponsePackage(orderService.listAll,HttpStatus.SC_OK).toResult
    }
  }

  def getAllAddresses:Action[AnyContent] = securedAction {secured =>
    onlyAdmin(secured){
      new ResponsePackage(addressService.listAll,HttpStatus.SC_OK).toResult
    }
  }

  def getOrderDetails(orderId:Int):Action[AnyContent] = securedAction { secured =>
    onlyAdmin(secured){
      new ResponsePackage(orderService.getOrderById(orderId),HttpStatus.SC_OK).toResult
    }
  }

  def getAllItems(orderId:Int):Action[AnyContent] = securedAction { secured =>
    onlyAdmin(secured){
      new ResponsePackage(itemService.listAllByOrderId(orderId),HttpStatus.SC_OK).toResult
    }
  }

  def deleteOrder(orderId:Int):Action[AnyContent] = securedAction { secured =>
    onlyAdmin(secured){
      orderService.deleteOrder(orderId)
      new ResponsePackage(s"The order with id ${orderId} has been permanently deleted!",HttpStatus.SC_OK).toResult
    }
  }

  def placeOrder():Action[AnyContent] = securedAction { secured =>
    onlyUser(secured){
      orderService.addOrder(provideToken(secured))
      new ResponsePackage("The order has been placed!",HttpStatus.SC_OK).toResult
    }
  }


}
