import org.scoverage.coveralls.GitHubActions
import org.scoverage.coveralls.Imports.CoverallsKeys.*
import sbt.Keys.*
import sbtassembly.AssemblyPlugin.autoImport.*

val scala3Version = "3.3.3"
val AkkaVersion = "2.9.2"
val AkkaHttpVersion = "10.6.2"

assembly / assemblyMergeStrategy := {
  case PathList("META-INF", _*) => MergeStrategy.discard
  case _                        => MergeStrategy.first
}

ThisBuild / scalaVersion := scala3Version
ThisBuild / version := "1.0"

Compile / mainClass := Some("hearthstoneMini.HearthstoneMini")
Compile / packageBin / mainClass := Some("hearthstoneMini.HearthstoneMini")

lazy val commonSettings = Seq(
  scalaVersion := scala3Version,
  resolvers += "Akka library repository".at("https://repo.akka.io/maven"),
  libraryDependencies ++= Seq(
    "org.scalactic" %% "scalactic" % "3.2.18",
    "org.scalatest" %% "scalatest" % "3.2.18" % "test",
    "org.scalamock" %% "scalamock" % "6.0.0" % "test",
    "com.typesafe.play" %% "play-json" % "2.10.4",
    "com.google.inject.extensions" % "guice-assistedinject" % "7.0.0",
    "net.codingwell" %% "scala-guice" % "7.0.0",
    "javax.inject" % "javax.inject" % "1",
    "org.scalafx" % "scalafx_3" % "20.0.0-R31",
    "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
    "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
    "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion
  ) ++ Seq(
    "base",
    "controls",
    "fxml",
    "graphics",
    "media",
    "swing",
    "web"
  ).map(m => "org.openjfx" % s"javafx-$m" % "20")
)

lazy val gui = project
  .in(file("./modules/gui"))
  .settings(
    name := "gui",
    commonSettings
  )
  .dependsOn(core, model)

lazy val tui = project
  .in(file("./modules/tui"))
  .settings(
    name := "tui",
    commonSettings
  )
  .dependsOn(core, model)

lazy val model = project
  .in(file("./modules/model"))
  .settings(
    name := "model",
    commonSettings
  )

lazy val persistence = project
  .in(file("./modules/persistence"))
  .settings(
    name := "persistence",
    commonSettings
  )
  .dependsOn(model % "compile->compile")

lazy val core = project
  .in(file("./modules/core"))
  .settings(
    name := "core",
    commonSettings
  )
  .dependsOn(
    model % "compile->compile",
    persistence % "compile->compile"
  )

lazy val root = project
  .in(file("."))
  .settings(
    name := "HearthstoneMini",
    version := "1.0",
    Compile / resourceDirectory := file(".") / "./src/main/resources",
    assembly / mainClass := Some("scala.HearthstoneMini"),
    assembly / assemblyJarName := "HearthstoneMini.jar",
    commonSettings
  )
  .dependsOn(
    core % "compile->compile;test->test",
    tui % "compile->compile;test->test",
    gui % "compile->compile;test->test",
    model % "compile->compile;test->test",
    persistence % "compile->compile;test->test"
  )
  .aggregate(core, tui, gui, model, persistence)
