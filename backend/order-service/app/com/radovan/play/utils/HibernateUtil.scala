package com.radovan.play.utils

import org.hibernate.SessionFactory
import org.hibernate.boot.MetadataSources
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import org.hibernate.service.ServiceRegistry

import com.zaxxer.hikari.{HikariConfig, HikariDataSource}

class HibernateUtil  {

  private val hikariDataSource: HikariDataSource = {
    val hikariConfig = new HikariConfig()
    hikariConfig.setJdbcUrl("jdbc:postgresql://localhost:5432/ecommerce-db")
    hikariConfig.setUsername("postgres")
    hikariConfig.setPassword("1111")
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
      .addAnnotatedClass(classOf[com.radovan.play.entity.OrderEntity])
      .addAnnotatedClass(classOf[com.radovan.play.entity.OrderItemEntity])
      .addAnnotatedClass(classOf[com.radovan.play.entity.OrderAddressEntity])
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
