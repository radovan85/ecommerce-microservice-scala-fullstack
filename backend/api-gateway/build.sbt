val ScalatraVersion = "3.1.1"

ThisBuild / scalaVersion := "2.13.16"
ThisBuild / organization := "com.radovan.scalatra"

enablePlugins(SbtTwirl, SbtWar, RevolverPlugin)

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
      "com.google.inject" % "guice" % "7.0.0"

    ),

    watchSources ++= Seq(
      baseDirectory.value / "src" / "main" / "scala",
      baseDirectory.value / "src" / "main" / "resources"
    )
  )


