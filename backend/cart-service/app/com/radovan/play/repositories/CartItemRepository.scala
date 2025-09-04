package com.radovan.play.repositories

import com.radovan.play.entity.CartItemEntity

trait CartItemRepository {

  def deleteAllByCartId(cartId: Integer): Unit

  def deleteAllByProductId(productId: Integer): Unit

  def findAllByCartId(cartId: Integer): Array[CartItemEntity]

  def findAllByProductId(productId: Integer): Array[CartItemEntity]

  def findById(itemId: Integer): Option[CartItemEntity]

  def deleteById(itemId: Integer): Unit

  def save(itemEntity: CartItemEntity): CartItemEntity
}
