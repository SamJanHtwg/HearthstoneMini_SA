ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.3"

lazy val gui = (project in file("modules/gui"))
  .settings(
    name := "gui",
    libraryDependencies ++= {
      Seq("base", "controls", "fxml", "graphics", "media", "swing", "web")
        .map(m => "org.openjfx" % s"javafx-$m" % "20")
    },
    libraryDependencies += "org.scalafx" % "scalafx_3" % "20.0.0-R31",
  )
  .dependsOn()


