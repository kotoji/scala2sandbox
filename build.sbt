import Dependencies._

ThisBuild / scalaVersion := "2.13.12"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.example"
ThisBuild / organizationName := "example"

lazy val root = (project in file("."))
  .settings(
    name := "scala2sandbox",
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-jdk-http-client" % "0.7.0",
      munit % Test
    ),
    scalacOptions ++= Seq("-Xsource:3")
  )

lazy val udfpcats = (project in file("udemy-fpcats"))
  .settings(
    name := "udfpcats",
    Compile / mainClass := Some("udfpcats.Main"),
    scalacOptions ++= Seq("-Xsource:3"),
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-effect" % "3.4.5",
      munit % Test
    )
  )
