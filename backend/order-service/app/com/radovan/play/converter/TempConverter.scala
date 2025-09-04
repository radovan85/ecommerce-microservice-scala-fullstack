package com.radovan.play.converter

import com.radovan.play.dto.{OrderAddressDto, OrderDto, OrderItemDto}
import com.radovan.play.entity.{OrderAddressEntity, OrderEntity, OrderItemEntity}
import com.radovan.play.repositories.{OrderAddressRepository, OrderItemRepository, OrderRepository}
import jakarta.inject.{Inject, Singleton}
import org.modelmapper.ModelMapper

import java.time.ZoneId
import java.time.format.DateTimeFormatter
import scala.collection.mutable.ArrayBuffer
import scala.jdk.CollectionConverters._

@Singleton
class TempConverter {

  private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
  private val zoneId = ZoneId.of("UTC")
  private var mapper:ModelMapper = _
  private var orderRepository:OrderRepository = _
  private var addressRepository:OrderAddressRepository = _
  private var itemRepository:OrderItemRepository = _

  @Inject
  private def initialize(mapper: ModelMapper,orderRepository: OrderRepository,
                         addressRepository: OrderAddressRepository,itemRepository: OrderItemRepository):Unit = {
    this.mapper = mapper
    this.orderRepository = orderRepository
    this.addressRepository = addressRepository
    this.itemRepository = itemRepository
  }

  def orderItemEntityToDto(itemEntity: OrderItemEntity):OrderItemDto = {
    val returnValue = mapper.map(itemEntity, classOf[OrderItemDto])
    val orderOption = Option(itemEntity.getOrder())
    orderOption match {
      case Some(orderEntity) => returnValue.setOrderId(orderEntity.getOrderId())
      case None =>
    }

    returnValue
  }

  def orderItemDtoToEntity(itemDto: OrderItemDto): OrderItemEntity = {
    val returnValue = mapper.map(itemDto, classOf[OrderItemEntity])
    val orderIdOption = Option(itemDto.getOrderId())
    orderIdOption match {
      case Some(orderId) =>
        orderRepository.findById(orderId) match {
          case Some(orderEntity) => returnValue.setOrder(orderEntity)
          case None =>
        }
      case None =>
    }

    returnValue
  }

  def orderEntityToDto(orderEntity:OrderEntity):OrderDto = {
    val returnValue = mapper.map(orderEntity,classOf[OrderDto])
    val createTimeOption = Option(orderEntity.getCreateTime())
    createTimeOption match {
      case Some(createTime) =>
        val createTimeZoned = createTime.toLocalDateTime.atZone(zoneId)
        val createTimeStr = createTimeZoned.format(formatter)
        returnValue.setCreateTime(createTimeStr)
      case None =>
    }

    val addressOption = Option(orderEntity.getAddress())
    addressOption match {
      case Some(addressEntity) => returnValue.setAddressId(addressEntity.getOrderAddressId())
      case None =>
    }

    val orderedItemsOption = Option(orderEntity.getOrderedItems())
    val orderedItemsIds = new ArrayBuffer[Integer]()
    orderedItemsOption match {
      case Some(orderedItems) =>
        orderedItems.asScala.foreach(itemEntity => {
          orderedItemsIds += itemEntity.getOrderItemId()
        })
      case None =>
    }

    returnValue.setOrderedItemsIds(orderedItemsIds.toArray)
    returnValue
  }

  def orderDtoToEntity(orderDto: OrderDto):OrderEntity = {
    val returnValue = mapper.map(orderDto,classOf[OrderEntity])
    val addressIdOption = Option(orderDto.getAddressId())
    addressIdOption match {
      case Some(addressId) =>
        addressRepository.findById(addressId) match {
          case Some(addressEntity) => returnValue.setAddress(addressEntity)
          case None =>
        }
      case None =>
    }

    val orderedItemsIdsOption = Option(orderDto.getOrderedItemsIds())
    val orderedItems = new ArrayBuffer[OrderItemEntity]()
    orderedItemsIdsOption match {
      case Some(orderedItemsIds) =>
        orderedItemsIds.foreach(itemId => {
          itemRepository.findById(itemId) match {
            case Some(itemEntity) => orderedItems += itemEntity
            case None =>
          }
        })
      case None =>
    }

    returnValue.setOrderedItems(orderedItems.asJava)
    returnValue
  }

  def orderAddressEntityToDto(addressEntity: OrderAddressEntity):OrderAddressDto = {
    val returnValue = mapper.map(addressEntity,classOf[OrderAddressDto])
    val orderOption = Option(addressEntity.getOrder())
    orderOption match {
      case Some(orderEntity) => returnValue.setOrderId(orderEntity.getOrderId())
      case None =>
    }

    returnValue
  }

  def orderAddressDtoToEntity(addressDto: OrderAddressDto):OrderAddressEntity = {
    val returnValue = mapper.map(addressDto,classOf[OrderAddressEntity])
    val orderIdOption = Option(addressDto.getOrderId())
    orderIdOption match {
      case Some(orderId) =>
        orderRepository.findById(orderId) match {
          case Some(orderEntity) => returnValue.setOrder(orderEntity)
          case None =>
        }
      case None =>
    }

    returnValue
  }
}
