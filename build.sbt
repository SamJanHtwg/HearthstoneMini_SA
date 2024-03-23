import sbt.Keys.libraryDependencies
val scala3Version = "3.3.1"

assemblyMergeStrategy in assembly := {
 case PathList("META-INF", _*) => MergeStrategy.discard
 case _                        => MergeStrategy.first
}

Compile/mainClass := Some("scala.HearthstoneMini")
mainClass in (Compile, packageBin) := Some("scala.HearthstoneMini")

lazy val root = project
  .in(file("."))
  .settings(
    name := "HearthstoneMini",
    version := "1.0",
    resourceDirectory in Compile := file(".") / "./src/main/resources",
    assembly / mainClass := Some("scala.HearthstoneMini"),
    assembly / assemblyJarName := "HearthstoneMini.jar",
   
    
    //fork := true,
    scalaVersion := scala3Version,

    libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.12",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.12" % "test",

    libraryDependencies += "org.scalafx" % "scalafx_3" % "20.0.0-R31",
    libraryDependencies ++= {
      // Determine OS version of JavaFX binaries
      // lazy val osName = System.getProperty("os.name") match {
      //   case n if n.startsWith("Linux") => "linux"
      //   case n if n.startsWith("Mac") => "mac"
      //   case n if n.startsWith("Windows") => "win"
      //   case _ => throw new Exception("Unknown platform!")
      // }
      Seq("base", "controls", "fxml", "graphics", "media", "swing", "web")
        .map(m => "org.openjfx" % s"javafx-$m" % "20")
    },
    
    libraryDependencies += "com.google.inject.extensions" % "guice-assistedinject" % "5.1.0",
    libraryDependencies += "net.codingwell" %% "scala-guice" % "5.1.1",

    libraryDependencies += "com.typesafe.play" %% "play-json" % "2.10.1",

    libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "2.1.0",

    jacocoReportSettings := JacocoReportSettings(
      "Jacoco Coverage Report",
      None,
      JacocoThresholds(),
      Seq(JacocoReportFormats.ScalaHTML, JacocoReportFormats.XML), // note XML formatter
      "utf-8"),
    jacocoExcludes := Seq("aview*", "model.playerComponent.PlayerInterface",
      "model.manaComponent.ManaInterface", "model.cardComponent.CardInterface",
      "model.cardareaComponent.CardAreaInterface",
      "model.fieldbarComponent.FieldbarInterface", "model.fieldComponent.FieldInterface",
      "model.gamebarComponent.GamebarInterface",
      "model.healthpointsComponent.HealthpointsInterface", "model.matrixComponent.MatrixInterface",
      "HearthstoneMini*")

  )



