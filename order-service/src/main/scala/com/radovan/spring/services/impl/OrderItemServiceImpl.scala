package com.radovan.spring.services.impl

import com.radovan.spring.converter.TempConverter
import com.radovan.spring.dto.OrderItemDto
import com.radovan.spring.repositories.OrderItemRepository
import com.radovan.spring.services.{OrderItemService, OrderService}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import scala.jdk.CollectionConverters._

@Service
class OrderItemServiceImpl extends OrderItemService {

  private var itemRepository:OrderItemRepository = _
  private var tempConverter:TempConverter = _
  private var orderService:OrderService = _

  @Autowired
  private def initialize(itemRepository: OrderItemRepository,tempConverter: TempConverter):Unit = {
    this.itemRepository = itemRepository
    this.tempConverter = tempConverter
  }

  @Transactional(readOnly = true)
  override def listAllByOrderId(orderId: Integer): Array[OrderItemDto] = {
    orderService.getOrderById(orderId)
    val allItems = itemRepository.listAllByOrderId(orderId).asScala
    allItems.collect{
      case itemEntity => tempConverter.orderItemEntityToDto(itemEntity)
    }.toArray
  }
}
