import sbt.Keys.libraryDependencies

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.3"

lazy val root = (project in file("."))
  .settings(
    name := "core",
    libraryDependencies += "com.google.inject.extensions" % "guice-assistedinject" % "5.1.0",
    libraryDependencies += "net.codingwell" %% "scala-guice" % "5.1.1",
    libraryDependencies += "com.typesafe.play" %% "play-json" % "2.10.1",
    libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "2.1.0"
  )
