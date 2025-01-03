package com.radovan.spring.converter

import com.radovan.spring.dto.{OrderAddressDto, OrderDto, OrderItemDto}
import com.radovan.spring.entity.{OrderAddressEntity, OrderEntity, OrderItemEntity}
import com.radovan.spring.repositories.{OrderAddressRepository, OrderItemRepository, OrderRepository}
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.time.ZoneId
import java.time.format.DateTimeFormatter
import scala.collection.mutable.ArrayBuffer
import scala.jdk.CollectionConverters._

@Component
class TempConverter {

  private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
  private val zoneId = ZoneId.of("UTC")
  private var mapper:ModelMapper = _
  private var orderRepository:OrderRepository = _
  private var orderItemRepository:OrderItemRepository = _
  private var addressRepository:OrderAddressRepository = _

  @Autowired
  private def initialize(mapper:ModelMapper,orderRepository: OrderRepository,
                         orderItemRepository: OrderItemRepository,addressRepository: OrderAddressRepository):Unit = {
    this.mapper = mapper
    this.orderRepository = orderRepository
    this.orderItemRepository = orderItemRepository
    this.addressRepository = addressRepository
  }

  def orderAddressEntityToDto(address:OrderAddressEntity):OrderAddressDto = {
    val returnValue = mapper.map(address, classOf[OrderAddressDto])
    val orderOption = Option(address.getOrder)
    if(orderOption.isDefined) returnValue.setOrderId(orderOption.get.getOrderId)
    returnValue
  }

  def orderAddressDtoToEntity(address:OrderAddressDto):OrderAddressEntity = {
    val returnValue = mapper.map(address, classOf[OrderAddressEntity])
    val orderIdOption = Option(address.getOrderId)
    orderIdOption match {
      case Some(orderId) =>
        val orderEntity = orderRepository.findById(orderId).orElse(null)
        if(orderEntity!=null) returnValue.setOrder(orderEntity)
      case None =>
    }
    returnValue
  }

  def orderItemEntityToDto(orderItem:OrderItemEntity):OrderItemDto = {
    val returnValue = mapper.map(orderItem, classOf[OrderItemDto])
    val orderOption = Option(orderItem.getOrder)
    if(orderOption.isDefined) returnValue.setOrderId(orderOption.get.getOrderId)
    returnValue
  }

  def orderItemDtoToEntity(orderItem:OrderItemDto):OrderItemEntity = {
    val returnValue = mapper.map(orderItem, classOf[OrderItemEntity])
    val orderIdOption = Option(orderItem.getOrderId)
    orderIdOption match {
      case Some(orderId) =>
        val orderEntity = orderRepository.findById(orderId).orElse(null)
        if(orderEntity!=null) returnValue.setOrder(orderEntity)
      case None =>
    }
    returnValue
  }

  def orderEntityToDto(order:OrderEntity):OrderDto = {
    val returnValue = mapper.map(order, classOf[OrderDto])
    val createdAtOption = Option(order.getCreatedAt)
    createdAtOption match {
      case Some(createdAt) =>
        val createdAtZoned = createdAt.toLocalDateTime.atZone(zoneId)
        val createdAtStr = createdAtZoned.format(formatter)
        returnValue.setCreatedAt(createdAtStr)
      case None =>
    }

    val orderedItemsOption = Option(order.getOrderedItems)
    val orderedItemsIds = new ArrayBuffer[Integer]()
    orderedItemsOption match {
      case Some(orderedItems) =>
        orderedItems.forEach(itemEntity => orderedItemsIds += itemEntity.getOrderItemId)
      case None =>
    }
    returnValue.setOrderedItemsIds(orderedItemsIds.toArray)

    val addressOption = Option(order.getAddress)
    if(addressOption.isDefined) returnValue.setAddressId(addressOption.get.getOrderAddressId)
    returnValue
  }

  def orderDtoToEntity(order:OrderDto):OrderEntity = {
    val returnValue = mapper.map(order, classOf[OrderEntity])
    val orderedItemsIdsOption = Option(order.getOrderedItemsIds)
    val orderedItems = new ArrayBuffer[OrderItemEntity]()
    orderedItemsIdsOption match {
      case Some(orderedItemsIds) =>
        orderedItemsIds.foreach(itemId => {
          val itemEntity = orderItemRepository.findById(itemId).orElse(null)
          if(itemEntity!=null) orderedItems += itemEntity
        })
      case None =>
    }
    returnValue.setOrderedItems(orderedItems.asJava)

    val addressIdOption = Option(order.getAddressId)
    addressIdOption match {
      case Some(addressId) =>
        val addressEntity = addressRepository.findById(addressId).orElse(null)
        if(addressEntity!=null) returnValue.setAddress(addressEntity)
      case None =>
    }

    returnValue
  }

}
