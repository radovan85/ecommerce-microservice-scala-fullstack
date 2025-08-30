package com.radovan.play.utils

import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import jakarta.inject.Singleton
import org.hibernate.SessionFactory
import org.hibernate.boot.registry.{StandardServiceRegistry, StandardServiceRegistryBuilder}
import org.hibernate.cfg.Configuration

@Singleton
class HibernateUtil {

  val sessionFactory: SessionFactory = buildSessionFactory()

  private def buildSessionFactory(): SessionFactory = {
    try {
      // 🔧 Konfiguracija Hikari pool-a
      val hikariConfig = new HikariConfig()
      hikariConfig.setJdbcUrl("jdbc:postgresql://localhost:5432/ecommerce-db")
      hikariConfig.setUsername("postgres")
      hikariConfig.setPassword("1111")
      hikariConfig.setDriverClassName("org.postgresql.Driver")
      hikariConfig.setMaximumPoolSize(10)
      hikariConfig.setMinimumIdle(2)
      hikariConfig.setIdleTimeout(600000)
      hikariConfig.setConnectionTimeout(30000)
      hikariConfig.setMaxLifetime(1800000)

      val hikariDataSource = new HikariDataSource(hikariConfig)

      // 🧠 Hibernate konfiguracija
      val configuration = new Configuration()
      configuration.getProperties.put("hibernate.connection.datasource", hikariDataSource)
      configuration.setProperty("hibernate.hbm2ddl.auto", "update")
      configuration.setProperty("hibernate.show_sql", "false")
      configuration.setProperty("hibernate.format_sql", "false")
      configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")


      // ➕ Dodaj entity klasu/e
      configuration.addAnnotatedClass(classOf[com.radovan.play.entity.ProductCategoryEntity])
      configuration.addAnnotatedClass(classOf[com.radovan.play.entity.ProductEntity])
      configuration.addAnnotatedClass(classOf[com.radovan.play.entity.ProductImageEntity])
      // Dodaj još ako treba...

      // 🧱 SessionFactory setup
      val serviceRegistry: StandardServiceRegistry =
        new StandardServiceRegistryBuilder()
          .applySettings(configuration.getProperties)
          .build()

      configuration.buildSessionFactory(serviceRegistry)
    } catch {
      case ex: Throwable =>
        System.err.println(s"Initial SessionFactory creation failed: $ex")
        throw new ExceptionInInitializerError(ex)
    }
  }

  def getSessionFactory: SessionFactory = sessionFactory
}
