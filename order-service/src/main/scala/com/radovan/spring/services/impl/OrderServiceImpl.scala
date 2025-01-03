package com.radovan.spring.services.impl

import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.radovan.spring.converter.{DeserializeConverter, TempConverter}
import com.radovan.spring.dto.{OrderAddressDto, OrderDto, OrderItemDto}
import com.radovan.spring.entity.OrderItemEntity
import com.radovan.spring.exceptions.{InstanceUndefinedException, OutOfStockException}
import com.radovan.spring.repositories.{OrderAddressRepository, OrderItemRepository, OrderRepository}
import com.radovan.spring.services.OrderService
import com.radovan.spring.utils.ServiceUrlProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpEntity, HttpMethod}
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate

import java.sql.Timestamp
import java.time.{Instant, ZoneId}
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.jdk.CollectionConverters._

@Service
class OrderServiceImpl extends OrderService {

  private val zoneId = ZoneId.of("UTC")
  private var orderRepository: OrderRepository = _
  private var orderAddressRepository: OrderAddressRepository = _
  private var orderItemRepository: OrderItemRepository = _
  private var deserializeConverter: DeserializeConverter = _
  private var tempConverter: TempConverter = _
  private var restTemplate: RestTemplate = _
  private var urlProvider: ServiceUrlProvider = _

  @Autowired
  private def initialize(orderRepository: OrderRepository, orderAddressRepository: OrderAddressRepository,
                         orderItemRepository: OrderItemRepository, deserializeConverter: DeserializeConverter,
                         tempConverter: TempConverter, restTemplate: RestTemplate,
                         urlProvider: ServiceUrlProvider): Unit = {
    this.orderRepository = orderRepository
    this.orderAddressRepository = orderAddressRepository
    this.orderItemRepository = orderItemRepository
    this.deserializeConverter = deserializeConverter
    this.tempConverter = tempConverter
    this.restTemplate = restTemplate
    this.urlProvider = urlProvider
  }

  @Transactional
  override def addOrder: OrderDto = {
    var customerMap = new mutable.HashMap[String, Any]()
    var cartMap = new mutable.HashMap[String, Any]()
    var shippingAddressMap = new mutable.HashMap[String, Any]()
    val customerResponse = deserializeConverter.getJsonNodeResponse(s"${urlProvider.getCustomerServiceUrl}/customers/currentCustomer")
    if (customerResponse.getStatusCode.is2xxSuccessful()) {
      customerMap = mutable.HashMap(deserializeConverter.deserializeJson(customerResponse.getBody.toString).asScala.toSeq: _*)
    } else {
      throw new InstanceUndefinedException(new Error("The customer response has not been found!"))
    }

    val cartIdOption = customerMap.get("cartId").map(_.toString.toInt)
    val cartId = cartIdOption.getOrElse(
      throw new InstanceUndefinedException(new Error("Cart id has not been found!"))
    )

    val shippingAddressIdOption = customerMap.get("shippingAddressId").map(_.toString.toInt)
    val shippingAddressId = shippingAddressIdOption.getOrElse(
      throw new InstanceUndefinedException(new Error("Cart id has not been found!"))
    )

    val cartResponse = deserializeConverter.getJsonNodeResponse(s"${urlProvider.getCartServiceUrl}/cart/validateCart/$cartId")
    if (cartResponse.getStatusCode.is2xxSuccessful()) {
      cartMap = mutable.HashMap(deserializeConverter.deserializeJson(cartResponse.getBody.toString).asScala.toSeq: _*)
    }

    val cartPriceOption = cartMap.get("cartPrice").map(_.toString.toFloat)
    val cartPrice = cartPriceOption.getOrElse(
      throw new InstanceUndefinedException(new Error("Cart price has not been found!"))
    )

    val orderDto = new OrderDto
    orderDto.setCartId(cartId)
    orderDto.setOrderPrice(cartPrice)
    val shippingAddressResponse = deserializeConverter.getJsonNodeResponse(s"${urlProvider.getCustomerServiceUrl}/addresses/addressDetails/$shippingAddressId")
    if (shippingAddressResponse.getStatusCode.is2xxSuccessful()) {
      shippingAddressMap = mutable.HashMap(deserializeConverter.deserializeJson(shippingAddressResponse.getBody.toString).asScala.toSeq: _*)
    }

    val orderAddress = new OrderAddressDto
    val addressOption = shippingAddressMap.get("address").map(_.toString)
    val address = addressOption.getOrElse(
      throw new InstanceUndefinedException(new Error("Address has not been found!"))
    )

    val cityOption = shippingAddressMap.get("city").map(_.toString)
    val city = cityOption.getOrElse(
      throw new InstanceUndefinedException(new Error("City has not been found!"))
    )
    val stateOption = shippingAddressMap.get("state").map(_.toString)
    val state = stateOption.getOrElse(
      throw new InstanceUndefinedException(new Error("State has not been found!"))
    )

    val countryOption = shippingAddressMap.get("country").map(_.toString)
    val country = countryOption.getOrElse(
      throw new InstanceUndefinedException(new Error("Country has not been found!"))
    )

    val postcodeOption = shippingAddressMap.get("postcode").map(_.toString)
    val postcode = postcodeOption.getOrElse(
      throw new InstanceUndefinedException(new Error("Postcode has not been found!"))
    )

    orderAddress.setAddress(address)
    orderAddress.setCity(city)
    orderAddress.setState(state)
    orderAddress.setCountry(country)
    orderAddress.setPostcode(postcode)
    val storedAddress = orderAddressRepository.save(tempConverter.orderAddressDtoToEntity(orderAddress))
    val currentTime = Instant.now().atZone(zoneId)
    val currentTimestamp = Timestamp.valueOf(currentTime.toLocalDateTime)

    val orderEntity = tempConverter.orderDtoToEntity(orderDto)
    orderEntity.setAddress(storedAddress)
    orderEntity.setCreatedAt(currentTimestamp)
    var storedOrder = orderRepository.save(orderEntity)

    val orderedItems = new ArrayBuffer[OrderItemEntity]()
    val cartItems = deserializeConverter.getJsonNodeArray(s"${urlProvider.getCartServiceUrl}/items/allItemsByCartId/$cartId")
    cartItems.foreach(cartItem => {
      val itemMap = mutable.HashMap(deserializeConverter.deserializeJson(cartItem.toString).asScala.toSeq: _*)
      val quantityOption = itemMap.get("quantity").map(_.toString.toInt)
      val quantity = quantityOption.getOrElse(
        throw new InstanceUndefinedException(new Error("The quantity has not been found!"))
      )

      val productIdOption = itemMap.get("productId").map(_.toString.toInt)
      val productId = productIdOption.getOrElse(
        throw new InstanceUndefinedException(new Error("Product id has not been found!"))
      )

      val productResponse = deserializeConverter.getJsonNodeResponse(s"${urlProvider.getProductServiceUrl}/products/productDetails/$productId")
      val productMap = mutable.HashMap(deserializeConverter.deserializeJson(productResponse.getBody.toString).asScala.toSeq: _*)
      val unitStockOption = productMap.get("unitStock").map(_.toString.toInt)
      var unitStock = unitStockOption.getOrElse(
        throw new InstanceUndefinedException(new Error("Unit stock has not been found!"))
      )
      val productNameOption = productMap.get("productName").map(_.toString)
      val productName = productNameOption.getOrElse(
        throw new InstanceUndefinedException(new Error("Product name has not been found!"))
      )
      if (quantity > unitStock) throw new OutOfStockException(new Error(s"There is a shortage of $productName in stock"))
      else {
        unitStock = unitStock - quantity
        productMap.put("unitStock", unitStock)
        val mapper = new ObjectMapper()
        val product = mapper.valueToTree[JsonNode](productMap.asJava)
        val productUrl = s"${urlProvider.getProductServiceUrl}/products/orderUpdateProduct/$productId"
        val requestEntity = new HttpEntity[JsonNode](product)
        restTemplate.exchange(productUrl, HttpMethod.PUT, requestEntity, classOf[Unit])
      }

      val orderItemDto = new OrderItemDto
      orderItemDto.setOrderId(storedOrder.getOrderId)
      val priceOption = itemMap.get("price") map (_.toString.toFloat)
      val price = priceOption.getOrElse(
        throw new InstanceUndefinedException(new Error("Price has not been found!"))
      )
      orderItemDto.setPrice(price)

      val discountOption = productMap.get("discount") map (_.toString.toFloat)
      val discount = discountOption.getOrElse(
        throw new InstanceUndefinedException(new Error("Discount has not been found!"))
      )
      orderItemDto.setProductDiscount(discount)
      orderItemDto.setProductName(productName)

      val productPriceOption = productMap.get("productPrice") map (_.toString.toFloat)
      val productPrice = productPriceOption.getOrElse(
        throw new InstanceUndefinedException(new Error("Product price has not been found!"))
      )
      orderItemDto.setProductPrice(productPrice)
      orderItemDto.setQuantity(quantity)
      val orderItemEntity = tempConverter.orderItemDtoToEntity(orderItemDto)
      orderedItems += orderItemRepository.save(orderItemEntity)

    })

    storedOrder.getOrderedItems.addAll(orderedItems.asJava)
    storedOrder = orderRepository.saveAndFlush(storedOrder)
    val clearCartUrl = s"${urlProvider.getCartServiceUrl}/cart/clearCart"
    val clearCartRequestEntity = new HttpEntity[String](clearCartUrl)
    restTemplate.exchange(clearCartUrl, HttpMethod.DELETE, clearCartRequestEntity, classOf[String])
    tempConverter.orderEntityToDto(storedOrder)

  }

  @Transactional(readOnly = true)
  override def getOrderById(orderId: Integer): OrderDto = {
    val orderEntity = orderRepository.findById(orderId)
      .orElseThrow(() => new InstanceUndefinedException(new Error("The order has not been found!")))
    tempConverter.orderEntityToDto(orderEntity)
  }

  @Transactional(readOnly = true)
  override def listAll: Array[OrderDto] = {
    val allOrders = orderRepository.findAll().asScala
    allOrders.collect {
      case orderEntity => tempConverter.orderEntityToDto(orderEntity)
    }.toArray
  }

  @Transactional(readOnly = true)
  override def listAllByCartId(cartId: Integer): Array[OrderDto] = {
    val allOrders = orderRepository.findAllByCartId(cartId).asScala
    allOrders.collect {
      case orderEntity => tempConverter.orderEntityToDto(orderEntity)
    }.toArray
  }

  @Transactional
  override def deleteOrder(orderId: Integer): Unit = {
    getOrderById(orderId)
    orderRepository.deleteById(orderId)
    orderRepository.flush()
  }

  @Transactional
  override def deleteAllByCartId(cartId: Integer): Unit = {
    val allOrders = listAllByCartId(cartId)
    allOrders.foreach(order => deleteOrder(order.getOrderId))
  }
}
