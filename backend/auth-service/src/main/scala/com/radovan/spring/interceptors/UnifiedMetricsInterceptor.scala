package com.radovan.spring.interceptors


import com.radovan.spring.services.PrometheusService
import jakarta.servlet.http.{HttpServletRequest, HttpServletResponse}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor

@Component
class UnifiedMetricsInterceptor extends HandlerInterceptor {

  @Autowired
  private var prometheus: PrometheusService = _


  override def preHandle(
                          request: HttpServletRequest,
                          response: HttpServletResponse,
                          handler: Any
                        ): Boolean = {
    val startTimeNs = System.nanoTime()
    request.setAttribute("startTimeNs", startTimeNs)

    // 📈 Broj zahteva odmah
    prometheus.increaseRequestCount()

    true
  }

  override def afterCompletion(
                                request: HttpServletRequest,
                                response: HttpServletResponse,
                                handler: Any,
                                ex: Exception
                              ): Unit = {
    val startTimeNs = request.getAttribute("startTimeNs").asInstanceOf[Long]
    val durationSec = (System.nanoTime() - startTimeNs) / 1e9

    // ⏱ Trajanje odgovora
    prometheus.recordResponseTime(durationSec)

    // 📊 Status klasifikacija
    prometheus.updateHttpStatusCount(response.getStatus)

    // 🧠 Bonus: klasifikacija po kontroleru/metodi
    handler match {
      case method: HandlerMethod =>
        val controllerName = method.getBeanType.getSimpleName
        val methodName = method.getMethod.getName
        prometheus.updateControllerMethodTags(controllerName, methodName)
      case _ => // ignoriši ako nije HandlerMethod
    }

  }
}
