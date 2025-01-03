package com.radovan.spring.config

import jakarta.servlet.{MultipartConfigElement, ServletContext}
import org.springframework.web.WebApplicationInitializer
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext
import org.springframework.web.servlet.DispatcherServlet

import java.nio.file.Files

class WebAppInitializer extends WebApplicationInitializer {

  override def onStartup(container: ServletContext): Unit = {
    val TMP_FOLDER = Files.createTempDirectory("myTempDir").toString
    val MAX_UPLOAD_SIZE = 5 * 1024 * 1024

    val dispatcherContext = new AnnotationConfigWebApplicationContext()
    dispatcherContext.register(classOf[SpringMvcConfiguration])

    val dispatcher = container.addServlet("Spring Initializer", new DispatcherServlet(dispatcherContext))
    dispatcher.setLoadOnStartup(1)
    dispatcher.addMapping("/")

    val multipartConfigElement = new MultipartConfigElement(TMP_FOLDER, MAX_UPLOAD_SIZE, MAX_UPLOAD_SIZE * 2L, MAX_UPLOAD_SIZE / 2)
    dispatcher.setMultipartConfig(multipartConfigElement)
  }
}