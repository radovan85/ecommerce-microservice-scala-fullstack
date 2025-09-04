package com.radovan.scalatra.modules

import com.google.inject.AbstractModule
import com.radovan.scalatra.brokers.{CustomerNatsListener, CustomerNatsSender}
import com.radovan.scalatra.converter.TempConverter
import com.radovan.scalatra.repositories.{CustomerRepository, ShippingAddressRepository}
import com.radovan.scalatra.repositories.impl.{CustomerRepositoryImpl, ShippingAddressRepositoryImpl}
import com.radovan.scalatra.services.{CustomerService, EurekaRegistrationService, EurekaServiceDiscovery, PrometheusService, ShippingAddressService}
import com.radovan.scalatra.services.impl.{CustomerServiceImpl, EurekaRegistrationServiceImpl, EurekaServiceDiscoveryImpl, PrometheusServiceImpl, ShippingAddressServiceImpl}
import com.radovan.scalatra.utils.{JwtUtil, NatsUtils, PublicKeyCache, ServiceUrlProvider}
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.prometheusmetrics.{PrometheusConfig, PrometheusMeterRegistry}
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.stream.Materializer

import scala.concurrent.ExecutionContext


class AutoBindModule extends AbstractModule {

  override def configure(): Unit = {
    val actorSystem = ActorSystem("my-system")
    bind(classOf[ActorSystem]).toInstance(actorSystem)

    // Pekko Materializer
    val materializer = Materializer(actorSystem)
    bind(classOf[Materializer]).toInstance(materializer)

    // ExecutionContext
    bind(classOf[ExecutionContext]).toInstance(actorSystem.dispatcher)

    bind(classOf[EurekaRegistrationService]).to(classOf[EurekaRegistrationServiceImpl]).asEagerSingleton()
    bind(classOf[EurekaServiceDiscovery]).to(classOf[EurekaServiceDiscoveryImpl]).asEagerSingleton()
    bind(classOf[CustomerService]).to(classOf[CustomerServiceImpl]).asEagerSingleton()
    bind(classOf[ShippingAddressService]).to(classOf[ShippingAddressServiceImpl]).asEagerSingleton()
    bind(classOf[PrometheusService]).to(classOf[PrometheusServiceImpl]).asEagerSingleton()
    bind(classOf[CustomerRepository]).to(classOf[CustomerRepositoryImpl]).asEagerSingleton()
    bind(classOf[ShippingAddressRepository]).to(classOf[ShippingAddressRepositoryImpl]).asEagerSingleton()
    bind(classOf[ServiceUrlProvider]).asEagerSingleton()
    bind(classOf[TempConverter]).asEagerSingleton()
    bind(classOf[JwtUtil]).asEagerSingleton()
    bind(classOf[NatsUtils]).asEagerSingleton()
    bind(classOf[PublicKeyCache]).asEagerSingleton()
    bind(classOf[CustomerNatsSender]).asEagerSingleton()
    bind(classOf[CustomerNatsListener]).asEagerSingleton()

    val prometheusRegistry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
    bind(classOf[PrometheusMeterRegistry]).toInstance(prometheusRegistry)
    bind(classOf[MeterRegistry]).toInstance(prometheusRegistry)
  }
}
