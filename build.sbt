import sbt.Keys.*
import com.typesafe.sbt.packager.docker._
import com.typesafe.sbt.packager.docker.DockerChmodType
import librarymanagement.InclExclRule

val scala3Version = "3.3.3"
val AkkaVersion = "2.9.3"
val AkkaHttpVersion = "10.6.2"

scalacOptions ++= Seq(
  "-Xignore-scala2-macros"
)

ThisBuild / scalaVersion := scala3Version
ThisBuild / version := "1.1.0"

ThisBuild / Gatling / publishArtifact := false
ThisBuild / GatlingIt / publishArtifact := false

Compile / mainClass := Some("hearthstoneMini.HearthstoneMini")
Compile / packageBin / mainClass := Some("hearthstoneMini.HearthstoneMini")

val gatlingExclude = Seq(
  ("com.typesafe.akka", "akka-actor_2.13"),
  ("org.scala-lang.modules", "scala-java8-compat_2.13"),
  ("com.typesafe.akka", "akka-slf4j_2.13"),
  ("org.scala-lang.modules","scala-parser-combinators_2.13"),
  ("org.scala-lang.modules", "scala-collection-compat_2.13")
).toVector.map((org_name: Tuple2[String, String]) =>
  InclExclRule(org_name._1, org_name._2)
)
val gatlingHigh =
  ("io.gatling.highcharts" % "gatling-charts-highcharts" % "3.11.3" % "test")
    .withExclusions(gatlingExclude)
val gatlingTest = ("io.gatling" % "gatling-test-framework" % "3.11.3" % "test")
  .withExclusions(gatlingExclude)

lazy val commonSettings = Seq(
  scalaVersion := scala3Version,
  resolvers += "Akka library repository".at("https://repo.akka.io/maven"),
  libraryDependencies ++= Seq(
    "org.scalactic" %% "scalactic" % "3.2.18",
    "org.scalatest" %% "scalatest" % "3.2.18" % "test",
    "org.mockito" % "mockito-core" % "5.12.0" % "test",
    "org.scalamock" %% "scalamock" % "6.0.0" % "test",
    "com.typesafe.akka" %% "akka-pki" % "2.9.3",
    ("com.typesafe.play" %% "play-json" % "2.10.4")
      .cross(CrossVersion.for3Use2_13),
    "com.google.inject.extensions" % "guice-assistedinject" % "7.0.0",
    "net.codingwell" %% "scala-guice" % "7.0.0",
    "javax.inject" % "javax.inject" % "1",
    "org.scalafx" % "scalafx_3" % "20.0.0-R31",
    "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
    "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
    "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion,
    "com.typesafe.akka" %% "akka-testkit" % AkkaVersion % Test,
    "com.typesafe.akka" %% "akka-http-testkit" % "10.6.2" % Test,
    "ch.qos.logback" % "logback-classic" % "1.1.3" % Runtime,
    "com.typesafe.slick" %% "slick" % "3.5.1",
    "org.postgresql" % "postgresql" % "42.6.0",
    "com.typesafe.slick" %% "slick-hikaricp" % "3.5.1",
    "org.slf4j" % "slf4j-nop" % "1.6.4",
    "com.github.tminglei" %% "slick-pg" % "0.22.1",
    "com.github.tminglei" %% "slick-pg_play-json" % "0.22.1",
    "com.typesafe.akka" %% "akka-stream-kafka" % "6.0.0"
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
  .enablePlugins(DockerCompose, JavaServerAppPackaging, GatlingPlugin)
  .settings(
    name := "persistence",
    commonSettings,
    libraryDependencies ++= Seq(
      ("org.mongodb.scala" %% "mongo-scala-driver" % "5.1.0")
        .cross(CrossVersion.for3Use2_13)
        ,
        gatlingHigh,
        gatlingTest
      ),
      dockerBaseImage := "hseeberger/scala-sbt:17.0.2_1.6.2_3.1.1",
      dockerExposedPorts := Seq(9021),
  )
  .dependsOn(model % "compile->compile", util % "compile->compile")

lazy val core = project
  .in(file("./modules/core"))
  .enablePlugins(DockerCompose, JavaServerAppPackaging, GatlingPlugin)
  .settings(
    name := "core",
    commonSettings,
    libraryDependencies ++= Seq(
      gatlingHigh,
      gatlingTest
    ),
    dockerBaseImage := "hseeberger/scala-sbt:17.0.2_1.6.2_3.1.1",
    dockerExposedPorts := Seq(9031)
  )
  .dependsOn(
    model % "compile->compile",
    persistence % "compile->compile",
    util % "compile->compile"
  )

lazy val util = project
  .in(file("./modules/util"))
  .settings(
    name := "util",
    commonSettings
  )

lazy val root = project
  .in(file("."))
  .enablePlugins(DockerCompose, JavaServerAppPackaging)
  .settings(
    name := "HearthstoneMini",
    version := "1.0",
    Compile / resourceDirectory := file(".") / "./src/main/resources",
    commonSettings
  )
  .dependsOn(
    core % "compile->compile;test->test",
    tui % "compile->compile;test->test",
    gui % "compile->compile;test->test",
    model % "compile->compile;test->test",
    persistence % "compile->compile;test->test",
    util % "compile->compile;test->test"
  )
  .aggregate(core, tui, gui, model, persistence)
