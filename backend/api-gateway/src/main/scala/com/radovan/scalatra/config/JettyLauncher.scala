package com.radovan.scalatra.config

import com.google.inject.{Guice, Injector}
import com.radovan.scalatra.controllers.{ApiGatewayController, HealthController, PrometheusController}
import com.radovan.scalatra.modules.AutoBindModule
import com.radovan.scalatra.services.{ApiGatewayService, EurekaRegistrationService, PrometheusService}
import com.radovan.scalatra.utils.ServiceUrlProvider
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
import org.eclipse.jetty.ee10.servlet.{ServletContextHandler, ServletHolder}
import org.eclipse.jetty.server.Server

object JettyLauncher {
  def main(args: Array[String]): Unit = {

    val injector: Injector = Guice.createInjector(

      new AutoBindModule
    )

    val port = System.getenv("SCALATRA_PORT").toInt
    val server = new Server(port)
    val context = new ServletContextHandler(ServletContextHandler.SESSIONS)
    context.setContextPath("/")
    server.setHandler(context)

    try {
      server.start()

      val apiGatewayService = injector.getInstance(classOf[ApiGatewayService])
      val eurekaRegistrationService = injector.getInstance(classOf[EurekaRegistrationService])
      val prometheusService = injector.getInstance(classOf[PrometheusService])
      val prometheusRegistry = injector.getInstance(classOf[PrometheusMeterRegistry])
      val urlProvider = injector.getInstance(classOf[ServiceUrlProvider])
      eurekaRegistrationService.registerService()


      val apiGatewayController = new ApiGatewayController(apiGatewayService,prometheusService)
      val healthController = new HealthController(prometheusService)
      val prometheusController = new PrometheusController(prometheusRegistry, urlProvider)

      context.addServlet(new ServletHolder("apiGatewayController", apiGatewayController), "/api/*")
      context.addServlet(new ServletHolder("healthController", healthController), "/api/health/*")
      context.addServlet(new ServletHolder("prometheusController", prometheusController), "/prometheus/*")

      println(s"✅ Server started at http://localhost:$port")
      println(s"✅ Health check: http://localhost:$port/api/health")

      server.join()
    } catch {
      case e: Exception =>
        e.printStackTrace()
        System.exit(1)
    }
  }
}
