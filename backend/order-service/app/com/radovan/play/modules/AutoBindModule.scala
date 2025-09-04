package com.radovan.play.modules

import com.google.inject.AbstractModule
import com.radovan.play.brokers.{OrderNatsListener, OrderNatsSender}
import com.radovan.play.converter.TempConverter
import com.radovan.play.repositories.impl.{OrderAddressRepositoryImpl, OrderItemRepositoryImpl, OrderRepositoryImpl}
import com.radovan.play.repositories.{OrderAddressRepository, OrderItemRepository, OrderRepository}
import com.radovan.play.services.impl.{EurekaRegistrationServiceImpl, EurekaServiceDiscoveryImpl, OrderAddressServiceImpl, OrderItemServiceImpl, OrderServiceImpl, PrometheusServiceImpl}
import com.radovan.play.services.{EurekaRegistrationService, EurekaServiceDiscovery, OrderAddressService, OrderItemService, OrderService, PrometheusService}
import com.radovan.play.utils.{JwtUtil, NatsUtils, PublicKeyCache, ServiceUrlProvider}
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.prometheusmetrics.{PrometheusConfig, PrometheusMeterRegistry}


class AutoBindModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[OrderItemService]).to(classOf[OrderItemServiceImpl]).asEagerSingleton()
    bind(classOf[OrderAddressService]).to(classOf[OrderAddressServiceImpl]).asEagerSingleton()
    bind(classOf[OrderService]).to(classOf[OrderServiceImpl]).asEagerSingleton()
    bind(classOf[PrometheusService]).to(classOf[PrometheusServiceImpl]).asEagerSingleton()
    bind(classOf[EurekaRegistrationService]).to(classOf[EurekaRegistrationServiceImpl]).asEagerSingleton()
    bind(classOf[EurekaServiceDiscovery]).to(classOf[EurekaServiceDiscoveryImpl]).asEagerSingleton()
    bind(classOf[OrderAddressRepository]).to(classOf[OrderAddressRepositoryImpl]).asEagerSingleton()
    bind(classOf[OrderItemRepository]).to(classOf[OrderItemRepositoryImpl]).asEagerSingleton()
    bind(classOf[OrderRepository]).to(classOf[OrderRepositoryImpl]).asEagerSingleton()
    bind(classOf[TempConverter]).asEagerSingleton()
    bind(classOf[JwtUtil]).asEagerSingleton()
    bind(classOf[NatsUtils]).asEagerSingleton()
    bind(classOf[PublicKeyCache]).asEagerSingleton()
    bind(classOf[ServiceUrlProvider]).asEagerSingleton()
    bind(classOf[OrderNatsSender]).asEagerSingleton()
    bind(classOf[OrderNatsListener]).asEagerSingleton()

    val prometheusRegistry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
    bind(classOf[PrometheusMeterRegistry]).toInstance(prometheusRegistry)
    bind(classOf[MeterRegistry]).toInstance(prometheusRegistry)
  }
}