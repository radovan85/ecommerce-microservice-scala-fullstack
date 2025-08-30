ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.15"

lazy val root = (project in file("."))
  .settings(
    name := "auth-service",


    libraryDependencies ++= Seq(
      "org.springframework.boot" % "spring-boot-starter-web" % "3.4.7",
      "org.springframework.boot" % "spring-boot-starter-security" % "3.4.7",
      "org.springframework.boot" % "spring-boot-starter-data-jpa" % "3.4.7",
      "org.springframework.cloud" % "spring-cloud-starter-netflix-eureka-client" % "4.2.2",
      "org.springframework.cloud" % "spring-cloud-starter-loadbalancer" % "4.2.2",
      "jakarta.validation" % "jakarta.validation-api" % "3.1.1",
      "org.hibernate.validator" % "hibernate-validator" % "8.0.2.Final",
      "com.fasterxml.jackson.core" % "jackson-databind" % "2.17.2",
      "com.fasterxml.jackson.dataformat" % "jackson-dataformat-xml" % "2.17.2",
      "org.postgresql" % "postgresql" % "42.7.7",
      "org.modelmapper" % "modelmapper" % "3.2.4",
      "io.jsonwebtoken" % "jjwt-api" % "0.12.5",
      "io.jsonwebtoken" % "jjwt-impl" % "0.12.5" % Runtime,
      "io.jsonwebtoken" % "jjwt-jackson" % "0.12.5" % Runtime,
      "com.auth0" % "java-jwt" % "4.5.0",
      "io.nats" % "jnats" % "2.21.4",
    )
  )
