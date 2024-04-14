import sbt.Keys.libraryDependencies
val scala3Version = "3.3.1"

assemblyMergeStrategy in assembly := {
 case PathList("META-INF", _*) => MergeStrategy.discard
 case _                        => MergeStrategy.first
}

Compile/mainClass := Some("hearthstoneMini.HearthstoneMini")
mainClass in (Compile, packageBin) := Some("hearthstoneMini.HearthstoneMini")

//lazy val gui = project
//  .in(file("modules/gui"))
//
//lazy val tui = project
//  .in(file("modules/tui"))

lazy val core = project
  .in(file("modules/core"))

lazy val root = project
  .in(file("."))
  .settings(
    name := "HearthstoneMini",
    version := "1.0",
    resourceDirectory in Compile := file(".") / "./src/main/resources",
    assembly / mainClass := Some("scala.HearthstoneMini"),
    assembly / assemblyJarName := "HearthstoneMini.jar",
   
    scalaVersion := scala3Version,

    libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.12",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.12" % "test",

    libraryDependencies += "com.typesafe.play" %% "play-json" % "2.10.1",

    libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "2.1.0",

    jacocoReportSettings := JacocoReportSettings(
      "Jacoco Coverage Report",
      None,
      JacocoThresholds(),
      Seq(JacocoReportFormats.ScalaHTML, JacocoReportFormats.XML), // note XML formatter
      "utf-8"),
//    jacocoExcludes := Seq(
//      "*Tui",
//      "*Interface",
//      "*view.*",
//      "*view.*.*",
//      "*view.*.*.*",
////      "HearthstoneMiniModule.scala",
//      "hearthstoneMini.HearthstoneMini.scala"
//    )
  )
  .dependsOn(core)
  .aggregate(core)



