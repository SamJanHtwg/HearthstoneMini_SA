import sbt.Keys.*
import sbtassembly.AssemblyPlugin.autoImport.*
import com.typesafe.sbt.packager.docker._
import com.typesafe.sbt.packager.docker.DockerChmodType

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
    "org.mockito" % "mockito-core" % "3.12.4" % "test",
    "org.scalamock" %% "scalamock" % "6.0.0" % "test",
    "com.typesafe.play" %% "play-json" % "2.10.4",
    "com.google.inject.extensions" % "guice-assistedinject" % "7.0.0",
    "net.codingwell" %% "scala-guice" % "7.0.0",
    "javax.inject" % "javax.inject" % "1",
    "org.scalafx" % "scalafx_3" % "20.0.0-R31",
    "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
    "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
    "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion,
    "com.typesafe.akka" %% "akka-testkit" % "2.9.2" % Test,
    "com.typesafe.akka" %% "akka-http-testkit" % "10.6.2" % Test,
    "ch.qos.logback" % "logback-classic" % "1.1.3" % Runtime
  ) ++ Seq(
    "base",
    "controls",
    "fxml",
    "graphics",
    "media",
    "swing",
    "web"
  ).map(m => "org.openjfx" % s"javafx-$m" % "20"),
  dockerLabels := Map("version" -> version.value),
  dockerExposedVolumes := Seq("/opt/docker/"),
  dockerUpdateLatest := true,
  dockerChmodType := DockerChmodType.UserGroupWriteExecute,
  Docker / daemonUserUid := None,
  Docker / daemonUser := "root"
)

lazy val gui = project
  .in(file("./modules/gui"))
  .enablePlugins(DockerCompose, JavaServerAppPackaging)
  .settings(
    name := "gui",
    commonSettings,
    dockerBaseImage := "nicolabeghin/liberica-openjdk-with-javafx-debian:17",
    dockerCommands ++= Seq(
      Cmd("RUN", "apt-get update"),
      Cmd(
        "RUN",
        "apt-get install -y libxrender1 libxtst6 libxi6 libgl1-mesa-glx libgtk-3-0 openjfx libgl1-mesa-dri libgl1-mesa-dev libcanberra-gtk-module libcanberra-gtk3-module default-jdk"
      )
    )
  )
  .dependsOn(core, model)

lazy val tui = project
  .in(file("./modules/tui"))
  .enablePlugins(DockerCompose, JavaServerAppPackaging)
  .settings(
    name := "tui",
    commonSettings,
    dockerBaseImage := "nicolabeghin/liberica-openjdk-with-javafx-debian:17"
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
  .enablePlugins(DockerCompose, JavaServerAppPackaging)
  .settings(
    name := "persistence",
    commonSettings,
    dockerBaseImage := "hseeberger/scala-sbt:17.0.2_1.6.2_3.1.1",
    dockerExposedPorts := Seq(9021)
  )
  .dependsOn(model % "compile->compile")

lazy val core = project
  .in(file("./modules/core"))
  .enablePlugins(DockerCompose, JavaServerAppPackaging)
  .settings(
    name := "core",
    commonSettings,
    dockerBaseImage := "hseeberger/scala-sbt:17.0.2_1.6.2_3.1.1",
    dockerExposedPorts := Seq(9031)
  )
  .dependsOn(
    model % "compile->compile",
    persistence % "compile->compile"
  )

lazy val root = project
  .in(file("."))
  .enablePlugins(DockerCompose, JavaServerAppPackaging)
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
