package com.radovan.spring.config

import java.util.Properties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.JpaVendorAdapter
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import com.zaxxer.hikari.HikariDataSource
import jakarta.persistence.EntityManagerFactory


@Configuration
@EnableTransactionManagement
class JpaPersistence {
  @Bean def entityManagerFactory: LocalContainerEntityManagerFactoryBean = {
    val em = new LocalContainerEntityManagerFactoryBean
    em.setDataSource(getHikariDataSource)
    em.setPackagesToScan("com.radovan.spring.entity")
    val vendorAdapter: JpaVendorAdapter = new HibernateJpaVendorAdapter
    em.setJpaVendorAdapter(vendorAdapter)
    em.setJpaProperties(additionalProperties)
    // Postavljanje interfejsa EntityManagerFactory
    em.setEntityManagerFactoryInterface(classOf[EntityManagerFactory])
    em
  }

  @Bean def getHikariDataSource: HikariDataSource = {
    val returnValue = new HikariDataSource
    val persistenceUrl = System.getenv("PERSISTENCE_URL")
    val persistencePassword = System.getenv("PERSISTENCE_PASSWORD")
    val persistenceUsername = System.getenv("PERSISTENCE_USERNAME")
    if (persistenceUsername == null || persistencePassword == null || persistenceUrl == null) throw new IllegalStateException("Database environment variables are missing!")
    returnValue.setJdbcUrl(persistenceUrl)
    returnValue.setUsername(persistenceUsername)
    returnValue.setPassword(persistencePassword)
    returnValue.setDriverClassName("org.postgresql.Driver")
    returnValue
  }

  @Bean def transactionManager: PlatformTransactionManager = {
    val transactionManager = new JpaTransactionManager
    transactionManager.setEntityManagerFactory(entityManagerFactory.getObject)
    transactionManager
  }

  @Bean def exceptionTranslation = new PersistenceExceptionTranslationPostProcessor

  private[config] def additionalProperties = {
    val properties = new Properties
    properties.setProperty("hibernate.hbm2ddl.auto", "update") // Kontrola kreiranja šeme
    properties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect") // Dialekt baze
    properties
  }
}