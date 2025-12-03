val ScalatraVersion = "3.1.2"

ThisBuild / scalaVersion := "2.13.17"
ThisBuild / organization := "com.radovan.scalatra"

enablePlugins(SbtTwirl, SbtWar, RevolverPlugin, JavaAppPackaging)

lazy val hello = (project in file("."))
  .settings(
    name := "api-gateway",
    version := "0.1.0-SNAPSHOT",

    fork := true,
    mainClass := Some("com.radovan.scalatra.config.JettyLauncher"),

    libraryDependencies ++= Seq(
      "org.scalatra" %% "scalatra-json" % "3.0.0-M5-jakarta",
      "org.scalatra" %% "scalatra-jakarta" % ScalatraVersion,
      "ch.qos.logback" % "logback-classic" % "1.5.6" % "runtime",
      "jakarta.servlet" % "jakarta.servlet-api" % "6.1.0" % "provided",
      "jakarta.enterprise" % "jakarta.enterprise.cdi-api" % "4.1.0",
      "jakarta.inject" % "jakarta.inject-api" % "2.0.1",
      "jakarta.annotation" % "jakarta.annotation-api" % "3.0.0",
      "jakarta.el" % "jakarta.el-api" % "6.0.1",
      "org.glassfish.expressly" % "expressly" % "6.0.0",
      "net.sf.flexjson" % "flexjson" % "3.3",
      "org.apache.httpcomponents.client5" % "httpclient5" % "5.5.1",
      "org.apache.pekko" %% "pekko-slf4j" % "1.2.1",
      "org.apache.pekko" %% "pekko-actor" % "1.2.1",
      "org.apache.pekko" %% "pekko-stream" % "1.2.1",
      "org.apache.pekko" %% "pekko-http" % "1.2.0",
      "org.apache.pekko" %% "pekko-http-spray-json" % "1.2.0",
      "com.google.inject" % "guice" % "7.0.0",
      "io.micrometer" % "micrometer-registry-prometheus" % "1.14.12"

    ),

    watchSources ++= Seq(
      baseDirectory.value / "src" / "main" / "scala",
      baseDirectory.value / "src" / "main" / "resources"
    )
  )
