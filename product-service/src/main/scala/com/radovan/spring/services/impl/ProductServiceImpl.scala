package com.radovan.spring.services.impl

import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.radovan.spring.converter.{DeserializeConverter, TempConverter}
import com.radovan.spring.dto.ProductDto
import com.radovan.spring.exceptions.InstanceUndefinedException
import com.radovan.spring.repositories.ProductRepository
import com.radovan.spring.services.{ProductCategoryService, ProductService}
import com.radovan.spring.utils.ServiceUrlProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpEntity, HttpMethod}
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate

import scala.jdk.CollectionConverters._

@Service
class ProductServiceImpl extends ProductService {

  private var productRepository: ProductRepository = _
  private var categoryService: ProductCategoryService = _
  private var deserializeConverter: DeserializeConverter = _
  private var tempConverter: TempConverter = _
  private var restTemplate: RestTemplate = _
  private var urlProvider: ServiceUrlProvider = _

  @Autowired
  private def initialize(productRepository: ProductRepository, categoryService: ProductCategoryService,
                         deserializeConverter: DeserializeConverter, tempConverter: TempConverter,
                         restTemplate: RestTemplate, urlProvider: ServiceUrlProvider): Unit = {
    this.productRepository = productRepository
    this.categoryService = categoryService
    this.deserializeConverter = deserializeConverter
    this.tempConverter = tempConverter
    this.restTemplate = restTemplate
    this.urlProvider = urlProvider
  }

  @Transactional
  override def addProduct(product: ProductDto): ProductDto = {
    categoryService.getCategoryById(product.getProductCategoryId)
    val storedProduct = productRepository.save(tempConverter.productDtoToEntity(product))
    tempConverter.productEntityToDto(storedProduct)
  }

  @Transactional(readOnly = true)
  override def getProductById(productId: Integer): ProductDto = {
    val productEntity = productRepository.findById(productId)
      .orElseThrow(() => new InstanceUndefinedException(new Error("The product has not been found!")))
    tempConverter.productEntityToDto(productEntity)
  }

  @Transactional
  def updateProduct(product: ProductDto, productId: Integer): ProductDto = {
    // Validacija kategorije
    categoryService.getCategoryById(product.getProductCategoryId)

    // Dohvatanje trenutnog proizvoda
    val currentProduct = getProductById(productId)

    // Dohvatanje svih stavki iz korpe za dati proizvod
    val allCartItems = deserializeConverter
      .getJsonNodeArray(s"${urlProvider.getCartServiceUrl}/items/allItemsByProductId/$productId")

    product.setProductId(currentProduct.getProductId)
    if (currentProduct.getImageId != null) {
      product.setImageId(currentProduct.getImageId)
    }

    // Ažuriranje proizvoda
    val updatedProduct = productRepository.saveAndFlush(tempConverter.productDtoToEntity(product))

    if (allCartItems.nonEmpty) {
      allCartItems.foreach { item =>
        val itemMap = deserializeConverter.deserializeJson(item.toString).asScala
        val itemIdOption = itemMap.get("cartItemId").map(_.toString.toInt)
        val itemId = itemIdOption.getOrElse(
          throw new InstanceUndefinedException(new Error("Item id has not been found"))
        )
        val quantityOption = itemMap.get("quantity").map(_.toString.toInt)
        val quantity = quantityOption.getOrElse(
          throw new InstanceUndefinedException(new Error("Quantity id has not been found"))
        )

        // Izračunavanje cene
        val discount = updatedProduct.getDiscount
        val productPrice = updatedProduct.getProductPrice
        val itemPrice = productPrice - ((productPrice * discount) / 100) * quantity

        // Kreiranje JSON objekta za ažuriranu stavku
        val updatedItemMap = Map(
          "cartItemId" -> itemId,
          "price" -> itemPrice,
          "quantity" -> quantity
        )

        val mapper = new ObjectMapper()
        val cartItemDto = mapper.valueToTree[JsonNode](updatedItemMap.asJava)

        // Pozivanje cart-service za ažuriranje stavke
        val cartItemUrl = s"${urlProvider.getCartServiceUrl}/items/updateItem/$itemId"
        val requestEntity = new HttpEntity[JsonNode](cartItemDto)
        restTemplate.exchange(cartItemUrl, HttpMethod.PUT, requestEntity, classOf[Void])
      }

      val refreshRequestEntity = new HttpEntity[Unit](null)
      val cartRefreshUrl = s"${urlProvider.getCartServiceUrl}/cart/refreshAllCarts"
      restTemplate.exchange(cartRefreshUrl, HttpMethod.PUT, refreshRequestEntity, classOf[Unit])
    }
    tempConverter.productEntityToDto(updatedProduct)
  }

  @Transactional
  def updateProduct(product: JsonNode, productId: Integer): ProductDto = {
    // Dohvatanje postojećeg proizvoda
    val existingProduct = getProductById(productId)

    // Parsiranje JsonNode u mapu
    val productMap = deserializeConverter.deserializeJson(product.toString).asScala.toMap

    // Ažuriranje količine na stanju
    val unitStockOption = productMap.get("unitStock").map(_.toString.toInt)
    val unitStock = unitStockOption.getOrElse(
      throw new InstanceUndefinedException(new Error("Unit stock has not been found"))
    )
    existingProduct.setUnitStock(unitStock)

    // Ažuriranje proizvoda u bazi
    val updatedProduct = productRepository.saveAndFlush(tempConverter.productDtoToEntity(existingProduct))

    // Konvertovanje ažuriranog entiteta nazad u DTO
    tempConverter.productEntityToDto(updatedProduct)
  }


  @Transactional
  def deleteProduct(productId: Integer): Unit = {
    // Proveri da li proizvod postoji
    getProductById(productId)

    // Priprema HTTP zahteva za brisanje
    val requestEntity = new HttpEntity[Unit](null)
    val deleteItemsUrl = s"${urlProvider.getCartServiceUrl}/items/clearAllByProductId/$productId"

    // Pozivanje REST servisa za brisanje stavki vezanih za proizvod
    restTemplate.exchange(deleteItemsUrl, HttpMethod.DELETE, requestEntity, classOf[Unit])

    // Brisanje proizvoda iz baze podataka
    productRepository.deleteById(productId)
    productRepository.flush()
  }

  @Transactional(readOnly = true)
  override def listAll: Array[ProductDto] = {
    val allProducts = productRepository.findAll().asScala
    allProducts.collect {
      case productEntity => tempConverter.productEntityToDto(productEntity)
    }.toArray
  }

  @Transactional(readOnly = true)
  override def listAllByCategoryId(categoryId: Integer): Array[ProductDto] = {
    val allProducts = productRepository.findAllByCategoryId(categoryId).asScala
    allProducts.collect {
      case productEntity => tempConverter.productEntityToDto(productEntity)
    }.toArray
  }

  @Transactional
  override def deleteProductsByCategoryId(categoryId: Integer): Unit = {
    val allProducts = listAllByCategoryId(categoryId)
    allProducts.foreach(productEntity => deleteProduct(productEntity.getProductId))
  }
}
