val ScalatraVersion = "3.1.1"

ThisBuild / scalaVersion := "2.13.16"
ThisBuild / organization := "com.radovan.scalatra"

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
      "org.apache.httpcomponents.client5" % "httpclient5" % "5.4.4",
      "org.apache.pekko" %% "pekko-slf4j" % "1.0.2",
      "org.apache.pekko" %% "pekko-actor" % "1.0.2",
      "org.apache.pekko" %% "pekko-stream" % "1.0.2",
      "org.apache.pekko" %% "pekko-http" % "1.0.1",
      "org.apache.pekko" %% "pekko-http-spray-json" % "1.0.1",
      "com.google.inject" % "guice" % "7.0.0",
      "jakarta.validation" % "jakarta.validation-api" % "3.1.1",
      "org.hibernate.validator" % "hibernate-validator" % "8.0.2.Final",
      "org.modelmapper" % "modelmapper" % "3.2.4",
      "org.hibernate.orm" % "hibernate-core" % "6.5.2.Final",
      "jakarta.persistence" % "jakarta.persistence-api" % "3.2.0",
      "com.zaxxer" % "HikariCP" % "5.1.0",
      "org.postgresql" % "postgresql" % "42.7.7",
      "com.fasterxml.jackson.core" % "jackson-databind" % "2.17.2",
      "com.fasterxml.jackson.dataformat" % "jackson-dataformat-xml" % "2.17.2",
      "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.17.2",
      "io.nats" % "jnats" % "2.21.4",
      "io.jsonwebtoken" % "jjwt-api" % "0.12.5",
      "io.jsonwebtoken" % "jjwt-impl" % "0.12.5" % "runtime",
      "io.jsonwebtoken" % "jjwt-jackson" % "0.12.5" % "runtime",
      "com.auth0" % "java-jwt" % "4.4.0",
      "com.github.ben-manes.caffeine" % "caffeine" % "3.2.0"

    ),

    watchSources ++= Seq(
      baseDirectory.value / "src" / "main" / "scala",
      baseDirectory.value / "src" / "main" / "resources"
    )
  )

