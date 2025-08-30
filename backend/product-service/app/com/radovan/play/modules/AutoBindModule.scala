package com.radovan.play.modules

import com.google.inject.AbstractModule
import com.radovan.play.brokers.{ProductNatsListener, ProductNatsSender}
import com.radovan.play.converter.TempConverter
import com.radovan.play.repositories.{ProductCategoryRepository, ProductImageRepository, ProductRepository}
import com.radovan.play.repositories.impl.{ProductCategoryRepositoryImpl, ProductImageRepositoryImpl, ProductRepositoryImpl}
import com.radovan.play.services.impl.{EurekaRegistrationServiceImpl, EurekaServiceDiscoveryImpl, ProductCategoryServiceImpl, ProductImageServiceImpl, ProductServiceImpl}
import com.radovan.play.services.{EurekaRegistrationService, EurekaServiceDiscovery, ProductCategoryService, ProductImageService, ProductService}
import com.radovan.play.utils.{JwtUtil, NatsUtils, PublicKeyCache, ServiceUrlProvider}


class AutoBindModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[ProductCategoryService]).to(classOf[ProductCategoryServiceImpl]).asEagerSingleton()
    bind(classOf[ProductImageService]).to(classOf[ProductImageServiceImpl]).asEagerSingleton()
    bind(classOf[ProductService]).to(classOf[ProductServiceImpl]).asEagerSingleton()
    bind(classOf[EurekaRegistrationService]).to(classOf[EurekaRegistrationServiceImpl]).asEagerSingleton()
    bind(classOf[EurekaServiceDiscovery]).to(classOf[EurekaServiceDiscoveryImpl]).asEagerSingleton()
    bind(classOf[ProductCategoryRepository]).to(classOf[ProductCategoryRepositoryImpl]).asEagerSingleton()
    bind(classOf[ProductImageRepository]).to(classOf[ProductImageRepositoryImpl]).asEagerSingleton()
    bind(classOf[ProductRepository]).to(classOf[ProductRepositoryImpl]).asEagerSingleton()
    bind(classOf[TempConverter]).asEagerSingleton()
    bind(classOf[JwtUtil]).asEagerSingleton()
    bind(classOf[NatsUtils]).asEagerSingleton()
    bind(classOf[PublicKeyCache]).asEagerSingleton()
    bind(classOf[ServiceUrlProvider]).asEagerSingleton()
    bind(classOf[ProductNatsSender]).asEagerSingleton()
    bind(classOf[ProductNatsListener]).asEagerSingleton()


  }
}