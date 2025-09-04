ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.16"

lazy val root = (project in file("."))
  .settings(
    name := "eureka-server-app"
  )

libraryDependencies ++= Seq(
  "org.springframework.boot" % "spring-boot-starter-web" % "3.4.7",
  "org.springframework.boot" % "spring-boot-devtools" % "3.4.7",
  "org.springframework.boot" % "spring-boot-starter-actuator" % "3.4.7",
  "com.google.code.gson" % "gson" % "2.12.1",
  "org.springframework.cloud" % "spring-cloud-starter-netflix-eureka-server" % "4.2.2",

)

enablePlugins(JavaAppPackaging)

