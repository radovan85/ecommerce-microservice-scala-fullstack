name := """product-service"""
organization := "com.radovan.play"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.16"

PlayKeys.devSettings := Seq("play.server.http.port" -> "9002")

libraryDependencies ++= Seq(
  guice,
  jdbc,
  ehcache,
  ws,
  "jakarta.validation" % "jakarta.validation-api" % "3.1.1",
  "org.hibernate.validator" % "hibernate-validator" % "8.0.2.Final",
  "jakarta.el" % "jakarta.el-api" % "6.0.1",
  "org.glassfish.expressly" % "expressly" % "6.0.0",
  "org.hibernate.orm" % "hibernate-core" % "6.5.2.Final",
  "jakarta.persistence" % "jakarta.persistence-api" % "3.2.0",
  "com.zaxxer" % "HikariCP" % "5.1.0",
  "org.postgresql" % "postgresql" % "42.7.7",
  "org.playframework.anorm" %% "anorm" % "2.8.1",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.17.2",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.17.2",
  "net.sf.flexjson" % "flexjson" % "3.3",
  "org.modelmapper" % "modelmapper" % "3.2.4",
  "org.apache.pekko" %% "pekko-slf4j" % "1.0.2",
  "org.apache.pekko" %% "pekko-actor" % "1.0.2",
  "com.github.ben-manes.caffeine" % "caffeine" % "3.2.0",
  "io.nats" % "jnats" % "2.21.4",
  "io.jsonwebtoken" % "jjwt-api" % "0.12.5",
  "io.jsonwebtoken" % "jjwt-impl" % "0.12.5" % "runtime",
  "io.jsonwebtoken" % "jjwt-jackson" % "0.12.5" % "runtime",
  "com.auth0" % "java-jwt" % "4.4.0",
  "com.github.ben-manes.caffeine" % "caffeine" % "3.2.0",
  "org.apache.httpcomponents.client5" % "httpclient5" % "5.4.4",
  "commons-io" % "commons-io" % "2.17.0",
  "io.micrometer" % "micrometer-registry-prometheus" % "1.14.10"
)

