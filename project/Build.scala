import sbt._
import sbt.Keys._
import com.github.siasia.WebPlugin._

object MyBuild extends Build {
  lazy val helloProject = Project("hello-gwt-sbt", file("."), settings = buildSettings)

  // lazy val Gwt = config("gwt") extend (Compile).hide

  val gwtModules = SettingKey[Seq[String]]("gwt-modules")
  val gwtTemporaryPath = SettingKey[File]("gwt-temporary-path")
  val gwtCompile = TaskKey[Unit]("gwt-compile", "Runs the GWT compiler to produce JavaScript")

  val buildSettings = Defaults.defaultSettings ++ webSettings ++ Seq(
    ivyConfigurations += Gwt,
    libraryDependencies ++= Seq(
      "com.google.gwt" % "gwt-user" % "2.3.0" % "provided",
      "com.google.gwt" % "gwt-dev" % "2.3.0" % "provided",
      "javax.validation" % "validation-api" % "1.0.0.GA" % "provided" withSources (),
      "com.google.gwt" % "gwt-servlet" % "2.3.0"),
    gwtModules := List("Hello_gwt_sbt").map("net.thunderklaus.hello_gwt_sbt." + _),
    gwtTemporaryPath <<= (target) { (target) => target / "gwt" },
    gwtCompile <<= (dependencyClasspath in Compile, javaSource in Compile, gwtModules, gwtTemporaryPath) map
      { (dependencyClasspath, javaSource, gwtModules, tempPath) =>
        {
          println("tempPath: " + tempPath + " abs: " + tempPath.absString)
          IO.createDirectory(tempPath)
          val command = "java -cp " + dependencyClasspath.map(_.data.toString).mkString(";") + ";" + javaSource + " com.google.gwt.dev.Compiler -war " + tempPath.absString + " " + gwtModules.mkString(" ")
          println("command: " + command)
          command !
        }
      },
    prepareWebapp <<= prepareWebapp.dependsOn(gwtCompile),
    webappResources <<= (webappResources, gwtTemporaryPath) { (w: PathFinder, g: File) => w +++ PathFinder(g) })

}
