package com.radovan.spring.services.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpEntity, HttpHeaders, HttpMethod, MediaType}
import org.springframework.stereotype.Service
import org.springframework.util.{LinkedMultiValueMap, MultiValueMap}
import org.springframework.web.client.RestTemplate
import org.springframework.web.multipart.MultipartFile
import com.fasterxml.jackson.databind.JsonNode
import com.radovan.spring.converter.DeserializeConverter
import com.radovan.spring.services.ProductImageService
import com.radovan.spring.utils.{MultipartFileResource, ServiceUrlProvider}

import java.io.IOException

@Service
class ProductImageServiceImpl extends ProductImageService {

  private var deserializeConverter: DeserializeConverter = _
  private var restTemplate: RestTemplate = _
  private var urlProvider: ServiceUrlProvider = _

  @Autowired
  private def initialize(deserializeConverter: DeserializeConverter, restTemplate: RestTemplate,
                         urlProvider: ServiceUrlProvider): Unit = {
    this.deserializeConverter = deserializeConverter
    this.restTemplate = restTemplate
    this.urlProvider = urlProvider
  }

  override def addImage(file: MultipartFile, productId: Integer): String = {
    val productUrl = s"${urlProvider.getProductServiceUrl}/products/storeImage/$productId"

    try {
      val headers = new HttpHeaders()
      headers.setContentType(MediaType.MULTIPART_FORM_DATA)

      val body = new LinkedMultiValueMap[String, Any]()
      body.add("file", new MultipartFileResource(file))

      val requestEntity = new HttpEntity[MultiValueMap[String, Any]](body, headers)
      val response = restTemplate.exchange(productUrl, HttpMethod.POST, requestEntity, classOf[String])

      response.getBody
    } catch {
      case e: IOException => throw new RuntimeException("Failed to convert file to resource", e)
    }
  }

  override def listAll:Array[JsonNode]  = {
    val url = s"${urlProvider.getProductServiceUrl}/products/allImages"
    deserializeConverter.getJsonNodeArray(url)
  }
}
