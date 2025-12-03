val ScalatraVersion = "3.1.2"

ThisBuild / scalaVersion := "2.13.17"
ThisBuild / organization := "com.radovan.scalatra"

enablePlugins(SbtTwirl, SbtWar, RevolverPlugin, JavaAppPackaging)

lazy val hello = (project in file("."))
  .settings(
    name := "customer-service",
    version := "0.1.0-SNAPSHOT",

    fork := true,
    mainClass := Some("com.radovan.scalatra.config.JettyLauncher"),


    libraryDependencies ++= Seq(
      "org.scalatra" %% "scalatra-json" % "3.0.0-M5-jakarta",
      "org.scalatra" %% "scalatra-jakarta" % ScalatraVersion,
      "ch.qos.logback" % "logback-classic" % "1.5.6" % "runtime",
      "jakarta.servlet" % "jakarta.servlet-api" % "6.0.0" % "provided",
      "jakarta.enterprise" % "jakarta.enterprise.cdi-api" % "4.1.0",
      "jakarta.inject" % "jakarta.inject-api" % "2.0.1",
      "jakarta.annotation" % "jakarta.annotation-api" % "3.0.0",
      "jakarta.el" % "jakarta.el-api" % "6.0.1",
      "org.glassfish.expressly" % "expressly" % "6.0.0",
      "net.sf.flexjson" % "flexjson" % "3.3",
      "org.apache.httpcomponents.client5" % "httpclient5" % "5.5.1",
      "org.apache.pekko" %% "pekko-actor" % "1.2.1",
      "org.apache.pekko" %% "pekko-actor-typed" % "1.2.1",
      "org.apache.pekko" %% "pekko-stream" % "1.2.1",
      "org.apache.pekko" %% "pekko-serialization-jackson" % "1.2.1",
      "org.apache.pekko" %% "pekko-protobuf-v3" % "1.2.1",
      "org.apache.pekko" %% "pekko-slf4j" % "1.2.1",
      "org.apache.pekko" %% "pekko-http-spray-json" % "1.2.0",
      "com.google.inject" % "guice" % "7.0.0",
      "jakarta.validation" % "jakarta.validation-api" % "3.1.1",
      "org.hibernate.validator" % "hibernate-validator" % "9.1.0.Final",
      "org.modelmapper" % "modelmapper" % "3.2.5",
      "org.hibernate.orm" % "hibernate-core" % "7.1.7.Final",
      "jakarta.persistence" % "jakarta.persistence-api" % "3.2.0",
      "com.zaxxer" % "HikariCP" % "5.1.0",
      "org.postgresql" % "postgresql" % "42.7.7",
      "com.fasterxml.jackson.core" % "jackson-databind" % "2.20.1",
      "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.20.1",
      "io.nats" % "jnats" % "2.24.0",
      "io.jsonwebtoken" % "jjwt-api" % "0.12.7",
      "io.jsonwebtoken" % "jjwt-impl" % "0.12.7" % "runtime",
      "io.jsonwebtoken" % "jjwt-jackson" % "0.12.7" % "runtime",
      "com.auth0" % "java-jwt" % "4.4.0",
      "com.github.ben-manes.caffeine" % "caffeine" % "3.2.2",
      "io.micrometer" % "micrometer-registry-prometheus" % "1.14.12"

    ),



    watchSources ++= Seq(
      baseDirectory.value / "src" / "main" / "scala",
      baseDirectory.value / "src" / "main" / "resources"
    )
  )

