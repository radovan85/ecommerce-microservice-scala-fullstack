package com.radovan.play.modules

import com.google.inject.AbstractModule
import org.modelmapper.ModelMapper
import org.modelmapper.config.Configuration.AccessLevel
import org.modelmapper.convention.MatchingStrategies

class InstanceModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[ModelMapper]).toInstance(getMapper)
  }

  def getMapper: ModelMapper = {
    val modelMapper = new ModelMapper()
    modelMapper.getConfiguration
      .setAmbiguityIgnored(true)
      .setFieldAccessLevel(AccessLevel.PRIVATE)
    modelMapper.getConfiguration.setMatchingStrategy(MatchingStrategies.STRICT)
    modelMapper
  }
}
