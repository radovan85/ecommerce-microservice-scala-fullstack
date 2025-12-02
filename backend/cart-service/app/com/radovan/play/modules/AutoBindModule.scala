package com.radovan.play.modules

import com.google.inject.AbstractModule
import com.radovan.play.brokers.{CartNatsListener, CartNatsSender}
import com.radovan.play.converter.TempConverter
import com.radovan.play.repositories.{CartItemRepository, CartRepository}
import com.radovan.play.repositories.impl.{CartItemRepositoryImpl, CartRepositoryImpl}
import com.radovan.play.services.{CartItemService, CartService, EurekaRegistrationService, EurekaServiceDiscovery, PrometheusService}
import com.radovan.play.services.impl.{CartItemServiceImpl, CartServiceImpl, EurekaRegistrationServiceImpl, EurekaServiceDiscoveryImpl, PrometheusServiceImpl}
import com.radovan.play.utils.{JwtUtil, NodeUtils, PublicKeyCache, ServiceUrlProvider}
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.prometheusmetrics.{PrometheusConfig, PrometheusMeterRegistry}


class AutoBindModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[CartItemService]).to(classOf[CartItemServiceImpl]).asEagerSingleton()
    bind(classOf[CartService]).to(classOf[CartServiceImpl]).asEagerSingleton()
    bind(classOf[EurekaRegistrationService]).to(classOf[EurekaRegistrationServiceImpl]).asEagerSingleton()
    bind(classOf[PrometheusService]).to(classOf[PrometheusServiceImpl]).asEagerSingleton()
    bind(classOf[EurekaServiceDiscovery]).to(classOf[EurekaServiceDiscoveryImpl]).asEagerSingleton()
    bind(classOf[CartItemRepository]).to(classOf[CartItemRepositoryImpl]).asEagerSingleton()
    bind(classOf[CartRepository]).to(classOf[CartRepositoryImpl]).asEagerSingleton()
    bind(classOf[TempConverter]).asEagerSingleton()
    bind(classOf[JwtUtil]).asEagerSingleton()
    bind(classOf[PublicKeyCache]).asEagerSingleton()
    bind(classOf[ServiceUrlProvider]).asEagerSingleton()
    bind(classOf[CartNatsSender]).asEagerSingleton()
    bind(classOf[CartNatsListener]).asEagerSingleton()
    bind(classOf[NodeUtils]).asEagerSingleton()

    val prometheusRegistry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
    bind(classOf[PrometheusMeterRegistry]).toInstance(prometheusRegistry)
    bind(classOf[MeterRegistry]).toInstance(prometheusRegistry)

  }
}