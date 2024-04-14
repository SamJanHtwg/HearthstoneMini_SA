ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.3"

lazy val core = (project in file("modules/core"))

lazy val tui = (project in file("modules/tui"))
  .settings(
    name := "tui"
  )
  .dependsOn(core)
