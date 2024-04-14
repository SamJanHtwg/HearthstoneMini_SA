import sbt.Keys.libraryDependencies
val scala3Version = "3.3.3"

assemblyMergeStrategy in assembly := {
 case PathList("META-INF", _*) => MergeStrategy.discard
 case _                        => MergeStrategy.first
}

ThisBuild / scalaVersion := scala3Version
ThisBuild / version := "1.0"

Compile / mainClass := Some("hearthstoneMini.HearthstoneMini")
mainClass in (Compile, packageBin) := Some("hearthstoneMini.HearthstoneMini")

lazy val commonDependencies = Seq(
    libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.12",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.12" % "test",

    libraryDependencies += "com.typesafe.play" %% "play-json" % "2.10.1",
    libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "2.1.0",

    libraryDependencies += "com.google.inject.extensions" % "guice-assistedinject" % "5.1.0",
    libraryDependencies += "net.codingwell" %% "scala-guice" % "5.1.1",

     libraryDependencies ++= {
      Seq("base", "controls", "fxml", "graphics", "media", "swing", "web")
        .map(m => "org.openjfx" % s"javafx-$m" % "20")
    },
    libraryDependencies += "org.scalafx" % "scalafx_3" % "20.0.0-R31",
)

lazy val gui = project
  .in(file("./modules/gui")).settings(
    name := "gui",
    commonDependencies,
  ).dependsOn(core)

lazy val tui = project
  .in(file("./modules/tui")).settings(
    name := "tui",
    commonDependencies,
  ).dependsOn(core)

lazy val core = project
  .in(file("./modules/core")).settings(
    name := "core",
    commonDependencies,
  )

lazy val root = project
  .in(file("."))
  .settings(
    name := "HearthstoneMini",
    version := "1.0",
    resourceDirectory in Compile := file(".") / "./src/main/resources",
    assembly / mainClass := Some("scala.HearthstoneMini"),
    assembly / assemblyJarName := "HearthstoneMini.jar",
   
    scalaVersion := scala3Version,
    commonDependencies,

    jacocoReportSettings := JacocoReportSettings(
      "Jacoco Coverage Report",
      None,
      JacocoThresholds(),
      Seq(JacocoReportFormats.ScalaHTML, JacocoReportFormats.XML), // note XML formatter
      "utf-8"),
   jacocoExcludes := Seq(
     "*Tui",
     "*Interface",
     "*view.*",
     "*view.*.*",
     "*view.*.*.*",
     "hearthstoneMini.HearthstoneMini.scala"
   )
  )
  .dependsOn(core, tui, gui)


