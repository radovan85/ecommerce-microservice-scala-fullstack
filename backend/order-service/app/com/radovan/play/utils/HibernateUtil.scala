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
      val dbUrl = System.getenv("DB_URL")
      val dbPassword = System.getenv("DB_PASSWORD")
      val dbUsername = System.getenv("DB_USERNAME")
      if (dbUrl == null || dbUsername == null || dbPassword == null) throw new IllegalStateException("Database environment variables are missing!")
      hikariConfig.setJdbcUrl(dbUrl)
      hikariConfig.setUsername(dbUsername)
      hikariConfig.setPassword(dbPassword)
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
      configuration.addAnnotatedClass(classOf[com.radovan.play.entity.OrderAddressEntity])
      configuration.addAnnotatedClass(classOf[com.radovan.play.entity.OrderItemEntity])
      configuration.addAnnotatedClass(classOf[com.radovan.play.entity.OrderEntity])
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
