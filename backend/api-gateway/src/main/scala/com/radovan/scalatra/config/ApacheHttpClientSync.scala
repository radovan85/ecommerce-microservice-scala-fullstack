package com.radovan.scalatra.config

import org.apache.hc.client5.http.classic.methods._
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.core5.http.io.HttpClientResponseHandler
import org.apache.hc.core5.http.io.entity.StringEntity
import org.apache.hc.core5.http.{ClassicHttpRequest, ClassicHttpResponse, ContentType}
import org.apache.hc.core5.http.io.entity.EntityUtils

import java.nio.charset.StandardCharsets

object ApacheHttpClientSync {

  private val client = HttpClients.createDefault()

  private val responseHandler: HttpClientResponseHandler[(Int, String)] = new HttpClientResponseHandler[(Int, String)] {
    override def handleResponse(response: ClassicHttpResponse): (Int, String) = {
      val statusCode = response.getCode
      val entity = response.getEntity
      val body = if (entity != null) EntityUtils.toString(entity, StandardCharsets.UTF_8) else ""
      (statusCode, body)
    }
  }


  private def applyStandardHeaders(request: ClassicHttpRequest, headers: Map[String, String]): Unit = {
    request.setHeader("Accept", "application/json")
    request.setHeader("User-Agent", "Apache-HttpClient/5.2")
    request.setHeader("Accept-Encoding", "gzip")
    headers.foreach { case (k, v) => request.setHeader(k, v) }
  }


  def post(url: String, body: String, headers: Map[String, String] = Map.empty): (Int, String) = {
    val request = new HttpPost(url)
    val entity = new StringEntity(body, ContentType.create("application/json", "UTF-8"))
    request.setEntity(entity)
    applyStandardHeaders(request, headers)

    client.execute(request, responseHandler)
  }

  def get(url: String, headers: Map[String, String] = Map.empty): (Int, String) = {
    val request = new HttpGet(url)
    applyStandardHeaders(request, headers)
    client.execute(request, responseHandler)
  }

  def put(url: String, body: String, headers: Map[String, String] = Map.empty): (Int, String) = {
    val request = new HttpPut(url)
    val entity = new StringEntity(body, ContentType.create("application/json", "UTF-8"))
    request.setEntity(entity)
    applyStandardHeaders(request, headers)

    client.execute(request, responseHandler)
  }



  def delete(url: String, headers: Map[String, String] = Map.empty): (Int, String) = {
    val request = new HttpDelete(url)
    applyStandardHeaders(request, headers)
    val response = client.execute(request)
    extractResponse(response)
  }

  def options(url: String, headers: Map[String, String] = Map.empty): (Int, String) = {
    val request = new HttpOptions(url)
    applyStandardHeaders(request, headers)
    val response = client.execute(request)
    extractResponse(response)
  }



  private def extractResponse(response: ClassicHttpResponse): (Int, String) = {
    try {
      val statusCode = response.getCode
      val entity = response.getEntity
      val body = if (entity != null) EntityUtils.toString(entity) else ""
      (statusCode, body)
    } finally {
      response.close()
    }
  }
}
