package com.radovan.scalatra.modules

import com.google.inject.AbstractModule
import org.modelmapper.{ModelMapper, config}
import org.modelmapper.config.Configuration
import org.modelmapper.convention.MatchingStrategies

class MapperModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[ModelMapper]).toInstance(createMapper())
  }

  def createMapper(): ModelMapper = {
    val mapper = new ModelMapper()
    val config: Configuration = mapper.getConfiguration

    config.setAmbiguityIgnored(true)
    config.setFieldAccessLevel(Configuration.AccessLevel.PRIVATE)
    config.setMatchingStrategy(MatchingStrategies.STRICT)

    mapper
  }
}

