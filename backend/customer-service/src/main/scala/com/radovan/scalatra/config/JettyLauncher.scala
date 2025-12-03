package com.radovan.scalatra.config

import com.google.inject.{Guice, Injector}
import com.radovan.scalatra.controllers.{CustomerController, HealthController, PrometheusController, ShippingAddressController}
import com.radovan.scalatra.modules.{AutoBindModule, InstanceModule}
import com.radovan.scalatra.services.EurekaRegistrationService
import org.eclipse.jetty.ee10.servlet.{ServletContextHandler, ServletHolder}
import org.eclipse.jetty.server.Server

object JettyLauncher {

  def main(args: Array[String]): Unit = {
    // Kreiraj Guice injector sa više modula, uključujući HibernateModule
    val injector: Injector = Guice.createInjector(
      new InstanceModule,
      new AutoBindModule      // Automatsko bindovanje servisa i kontrolera
    )


    val port = System.getenv("SCALATRA_PORT").toInt
    val server = new Server(port)
    val context = new ServletContextHandler(ServletContextHandler.SESSIONS)
    context.setContextPath("/")
    server.setHandler(context)

    try {
      server.start()

      // Registruj servis u Eureki
      val eurekaRegistrationService = injector.getInstance(classOf[EurekaRegistrationService])
      eurekaRegistrationService.registerService()

      // Dohvati kontrolere iz Guice injector-a
      val customerController = injector.getInstance(classOf[CustomerController])
      val healthController = injector.getInstance(classOf[HealthController])
      val addressController = injector.getInstance(classOf[ShippingAddressController])
      val prometheusController = injector.getInstance(classOf[PrometheusController])

      // Dodaj kontrolere kao servlet-e na odgovarajuće rute
      context.addServlet(new ServletHolder("customerController", customerController), "/api/customers/*")
      context.addServlet(new ServletHolder("healthController", healthController), "/api/health/*")
      context.addServlet(new ServletHolder("addressController", addressController), "/api/addresses/*")
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
