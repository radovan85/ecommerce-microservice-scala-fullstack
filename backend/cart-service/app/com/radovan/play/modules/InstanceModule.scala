package com.radovan.play.modules

import com.google.inject.AbstractModule
import com.radovan.play.providers.{HibernateProvider, NatsUtilsProvider}
import com.radovan.play.utils.NatsUtils
import org.hibernate.SessionFactory
import org.modelmapper.ModelMapper
import org.modelmapper.config.Configuration.AccessLevel
import org.modelmapper.convention.MatchingStrategies

class InstanceModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[SessionFactory]).toProvider(classOf[HibernateProvider]).asEagerSingleton()
    bind(classOf[ModelMapper]).toInstance(getMapper)
    bind(classOf[NatsUtils]).toProvider(classOf[NatsUtilsProvider]).asEagerSingleton()
  }

  def getMapper: ModelMapper = {
    val mapper = new ModelMapper()
    mapper.getConfiguration
      .setAmbiguityIgnored(true)
      .setFieldAccessLevel(AccessLevel.PRIVATE)
      .setMatchingStrategy(MatchingStrategies.STRICT)
    mapper
  }

}
