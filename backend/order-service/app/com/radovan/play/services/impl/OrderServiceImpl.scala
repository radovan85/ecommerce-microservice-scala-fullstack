package com.radovan.play.services.impl

import com.fasterxml.jackson.databind.node.ObjectNode
import com.radovan.play.brokers.OrderNatsSender
import com.radovan.play.converter.TempConverter
import com.radovan.play.dto.{OrderAddressDto, OrderDto, OrderItemDto}
import com.radovan.play.entity.OrderItemEntity
import com.radovan.play.exceptions.{InstanceUndefinedException, OutOfStockException}
import com.radovan.play.repositories.{OrderAddressRepository, OrderItemRepository, OrderRepository}
import com.radovan.play.services.OrderService
import jakarta.inject.{Inject, Singleton}

import java.sql.Timestamp
import java.time.{Instant, ZoneId}
import scala.collection.mutable.ArrayBuffer
import scala.jdk.CollectionConverters._

@Singleton
class OrderServiceImpl extends OrderService {

  private var orderRepository: OrderRepository = _
  private var tempConverter: TempConverter = _
  private var natsSender: OrderNatsSender = _
  private var itemRepository: OrderItemRepository = _
  private var addressRepository: OrderAddressRepository = _
  private val zoneId = ZoneId.of("UTC")

  @Inject
  private def initialize(orderRepository: OrderRepository, tempConverter: TempConverter,
                         natsSender: OrderNatsSender, itemRepository: OrderItemRepository,
                         addressRepository: OrderAddressRepository): Unit = {
    this.orderRepository = orderRepository
    this.tempConverter = tempConverter
    this.natsSender = natsSender
    this.itemRepository = itemRepository
    this.addressRepository = addressRepository
  }

  override def addOrder(jwtToken: String): OrderDto = {
    val customerNode = natsSender.retrieveCurrentCustomer(jwtToken)
    val cartId = customerNode.get("cartId").asInt()
    val addressId = customerNode.get("shippingAddressId").asInt();
    val cartNode = natsSender.validateCart(cartId, jwtToken)
    val addressNode = natsSender.retrieveAddress(addressId, jwtToken)
    val orderAddressDto = new OrderAddressDto
    orderAddressDto.setAddress(addressNode.get("address").asText())
    orderAddressDto.setCity(addressNode.get("city").asText())
    orderAddressDto.setState(addressNode.get("state").asText())
    orderAddressDto.setPostcode(addressNode.get("postcode").asText())
    orderAddressDto.setCountry(addressNode.get("country").asText())
    val storedAddress = addressRepository.save(tempConverter.orderAddressDtoToEntity(orderAddressDto))
    val orderDto = new OrderDto
    orderDto.setCartId(cartId)
    orderDto.setOrderPrice(cartNode.get("cartPrice").floatValue())
    orderDto.setAddressId(storedAddress.getOrderAddressId())
    val orderEntity = tempConverter.orderDtoToEntity(orderDto)
    val currentTime = Instant.now().atZone(zoneId)
    orderEntity.setCreateTime(Timestamp.valueOf(currentTime.toLocalDateTime))
    val storedOrder = orderRepository.save(orderEntity)
    val cartItemsNode = natsSender.retrieveCartItems(cartId, jwtToken)
    val orderedItems = new ArrayBuffer[OrderItemEntity]()
    cartItemsNode.foreach(itemNode => {
      val productId = itemNode.get("productId").asInt()
      val quantity = itemNode.get("quantity").asInt()
      val productNode = natsSender.retrieveProductFromBroker(productId, jwtToken)
      val innerProductNode = productNode.get("product")
      val productName = innerProductNode.get("productName").asText()
      val unitStock = innerProductNode.get("unitStock").asInt()
      val productPrice = innerProductNode.get("productPrice").floatValue()
      val productDiscount = innerProductNode.get("discount").floatValue()
      if (quantity > unitStock) throw new OutOfStockException(s"Not enough stock for $productName")
      val updatedProduct = innerProductNode.deepCopy().asInstanceOf[ObjectNode]
      updatedProduct.put("unitStock", unitStock - quantity)
      natsSender.updateProductViaBroker(updatedProduct, productId, jwtToken)

      val orderItemDto = new OrderItemDto
      orderItemDto.setOrderId(storedOrder.getOrderId())
      orderItemDto.setPrice(itemNode.get("price").floatValue())
      orderItemDto.setQuantity(quantity)
      orderItemDto.setProductName(productName)
      orderItemDto.setProductPrice(productPrice)
      orderItemDto.setProductDiscount(productDiscount)
      val storedItem = itemRepository.save(tempConverter.orderItemDtoToEntity(orderItemDto))
      orderedItems += storedItem
    }
    )

    storedOrder.getOrderedItems().addAll(orderedItems.asJava)
    orderRepository.save(storedOrder)

    natsSender.removeAllByCartId(cartId, jwtToken)
    natsSender.refreshCartState(cartId, jwtToken)
    tempConverter.orderEntityToDto(storedOrder)
  }

  override def getOrderById(orderId: Int): OrderDto = {
    orderRepository.findById(orderId) match {
      case Some(orderEntity) => tempConverter.orderEntityToDto(orderEntity)
      case None => throw new InstanceUndefinedException("The order has not been found!")
    }
  }

  override def listAll: Array[OrderDto] = {
    orderRepository.findAll.collect {
      case orderEntity => tempConverter.orderEntityToDto(orderEntity)
    }
  }

  override def listAllByCartId(cartId: Int): Array[OrderDto] = {
    orderRepository.findAllByCartId(cartId).collect {
      case orderEntity => tempConverter.orderEntityToDto(orderEntity)
    }
  }

  override def deleteOrder(orderId: Int): Unit = {
    getOrderById(orderId)
    orderRepository.deleteById(orderId)
  }

  override def deleteAllByCartId(cartId: Int): Unit = {
    listAllByCartId(cartId).foreach(order => deleteOrder(order.getOrderId()))
  }
}
