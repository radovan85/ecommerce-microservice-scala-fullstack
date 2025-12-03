package com.radovan.play.utils

import org.hibernate.SessionFactory
import org.hibernate.boot.MetadataSources
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import org.hibernate.service.ServiceRegistry

import com.zaxxer.hikari.{HikariConfig, HikariDataSource}

class HibernateUtil  {

  private val hikariDataSource: HikariDataSource = {
    val hikariConfig = new HikariConfig()
    val persistenceUrl = System.getenv("PERSISTENCE_URL")
    val persistencePassword = System.getenv("PERSISTENCE_PASSWORD")
    val persistenceUsername = System.getenv("PERSISTENCE_USERNAME")
    if (persistenceUsername == null || persistencePassword == null || persistenceUrl == null) throw new IllegalStateException("Database environment variables are missing!")
    hikariConfig.setJdbcUrl(persistenceUrl)
    hikariConfig.setUsername(persistenceUsername)
    hikariConfig.setPassword(persistencePassword)
    hikariConfig.setDriverClassName("org.postgresql.Driver")
    hikariConfig.setMinimumIdle(2)
    hikariConfig.setIdleTimeout(600000)
    hikariConfig.setMaximumPoolSize(10)
    hikariConfig.setConnectionTimeout(30000)
    hikariConfig.setMaxLifetime(1800000)
    new HikariDataSource(hikariConfig)
  }

  private val serviceRegistry: ServiceRegistry =
    new StandardServiceRegistryBuilder()
      .applySetting("hibernate.boot.allow_jdbc_metadata_access", "false")
      .applySetting("hibernate.hbm2ddl.auto", "update")
      .applySetting("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")
      .applySetting("hibernate.connection.datasource", hikariDataSource)
      .applySetting("hibernate.show_sql", "false")
      .applySetting("hibernate.format_sql", "false")
      .build()

  private val sessionFactory: SessionFactory =
    new MetadataSources(serviceRegistry)
      .addAnnotatedClass(classOf[com.radovan.play.entity.ProductEntity])
      .addAnnotatedClass(classOf[com.radovan.play.entity.ProductImageEntity])
      .addAnnotatedClass(classOf[com.radovan.play.entity.ProductCategoryEntity])
      .buildMetadata()
      .buildSessionFactory()

  def getSessionFactory: SessionFactory = sessionFactory


  def shutdown(): Unit = {
    println("Shutting down HibernateUtil...")
    if (sessionFactory != null) {
      sessionFactory.close()
    }
    if (hikariDataSource != null) {
      hikariDataSource.close()
    }
  }
}
