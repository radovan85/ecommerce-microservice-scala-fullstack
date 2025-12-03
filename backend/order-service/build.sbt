name := """order-service"""
organization := "com.radovan.play"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.18"

libraryDependencies ++= Seq(
  guice,
  jdbc,
  ehcache,
  ws,
  "jakarta.validation" % "jakarta.validation-api" % "3.1.1",
  "org.hibernate.validator" % "hibernate-validator" % "9.1.0.Final",
  "jakarta.annotation" % "jakarta.annotation-api" % "3.0.0",
  "jakarta.el" % "jakarta.el-api" % "6.0.1",
  "org.glassfish.expressly" % "expressly" % "6.0.0",
  "org.hibernate.orm" % "hibernate-core" % "7.1.7.Final",
  "jakarta.persistence" % "jakarta.persistence-api" % "3.2.0",
  "com.zaxxer" % "HikariCP" % "7.0.2",
  "org.postgresql" % "postgresql" % "42.7.7",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.20.1",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.20.1",
  "net.sf.flexjson" % "flexjson" % "3.3",
  "org.modelmapper" % "modelmapper" % "3.2.5",
  "io.nats" % "jnats" % "2.24.0",
  "org.apache.httpcomponents.client5" % "httpclient5" % "5.5.1",
  "io.jsonwebtoken" % "jjwt-api" % "0.12.7",
  "io.jsonwebtoken" % "jjwt-impl" % "0.12.7" % "runtime",
  "io.jsonwebtoken" % "jjwt-jackson" % "0.12.7" % "runtime",
  "com.github.ben-manes.caffeine" % "caffeine" % "3.2.2",
  "io.micrometer" % "micrometer-registry-prometheus" % "1.14.12"
)

dependencyOverrides ++= Seq(
  "org.apache.pekko" %% "pekko-actor" % "1.2.1",
  "org.apache.pekko" %% "pekko-actor-typed" % "1.2.1",
  "org.apache.pekko" %% "pekko-stream" % "1.2.1",
  "org.apache.pekko" %% "pekko-serialization-jackson" % "1.2.1",
  "org.apache.pekko" %% "pekko-protobuf-v3" % "1.2.1",
  "org.apache.pekko" %% "pekko-slf4j" % "1.2.1"
)
