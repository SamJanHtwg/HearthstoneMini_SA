import org.scoverage.coveralls.GitHubActions
import org.scoverage.coveralls.Imports.CoverallsKeys.*
import sbt.Keys.*
import sbtassembly.AssemblyPlugin.autoImport.*

val scala3Version = "3.3.3"

assembly / assemblyMergeStrategy := {
  case PathList("META-INF", _*) => MergeStrategy.discard
  case _                        => MergeStrategy.first
}

ThisBuild / scalaVersion := scala3Version
ThisBuild / version := "1.0"

Compile / mainClass := Some("hearthstoneMini.HearthstoneMini")
Compile / packageBin / mainClass := Some("hearthstoneMini.HearthstoneMini")

lazy val commonDependencies = Seq(
  libraryDependencies ++= Seq(
    "org.scalactic" %% "scalactic" % "3.2.18",
    "org.scalatest" %% "scalatest" % "3.2.18" % "test",
    "com.typesafe.play" %% "play-json" % "2.10.4",
    "org.scala-lang.modules" %% "scala-xml" % "2.2.0",
    "com.google.inject.extensions" % "guice-assistedinject" % "7.0.0",
    "net.codingwell" %% "scala-guice" % "7.0.0",
    "javax.inject" % "javax.inject" % "1",
    "org.scalafx" % "scalafx_3" % "20.0.0-R31"
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
    commonDependencies
  )
  .dependsOn(core, model)

lazy val tui = project
  .in(file("./modules/tui"))
  .settings(
    name := "tui",
    commonDependencies
  )
  .dependsOn(core, model)

lazy val model = project
  .in(file("./modules/model"))
  .settings(
    name := "model",
    commonDependencies
  )

lazy val core = project
  .in(file("./modules/core"))
  .settings(
    name := "core",
    commonDependencies
  )
  .dependsOn(model % "compile->compile")

lazy val root = project
  .in(file("."))
  .settings(
    name := "HearthstoneMini",
    version := "1.0",
    Compile / resourceDirectory := file(".") / "./src/main/resources",
    assembly / mainClass := Some("scala.HearthstoneMini"),
    assembly / assemblyJarName := "HearthstoneMini.jar",
    scalaVersion := scala3Version,
    commonDependencies,
  )
  .enablePlugins(JacocoCoverallsPlugin)
  .dependsOn(
    core % "compile->compile;test->test",
    tui % "compile->compile;test->test",
    gui % "compile->compile;test->test",
    model % "compile->compile;test->test"
  )
  .aggregate(core, tui, gui, model)
