package com.radovan.play.services.impl

import com.radovan.play.converter.TempConverter
import com.radovan.play.dto.OrderItemDto
import com.radovan.play.repositories.OrderItemRepository
import com.radovan.play.services.OrderItemService
import jakarta.inject.{Inject, Singleton}

@Singleton
class OrderItemServiceImpl extends OrderItemService{

  private var itemRepository:OrderItemRepository = _
  private var tempConverter:TempConverter = _

  @Inject
  private def initialize(itemRepository: OrderItemRepository,tempConverter: TempConverter):Unit = {
    this.itemRepository = itemRepository
    this.tempConverter = tempConverter
  }

  override def listAllByOrderId(orderId: Int): Array[OrderItemDto] = {
    itemRepository.findAllByOrderId(orderId).collect{
      case itemEntity => tempConverter.orderItemEntityToDto(itemEntity)
    }
  }
}
