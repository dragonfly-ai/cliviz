ThisBuild / scalaVersion := "3.1.0"
ThisBuild / publishTo := Some( Resolver.file( "file",  new File("/var/www/maven" ) ) )
ThisBuild / resolvers += "ai.dragonfly.code" at "https://code.dragonfly.ai/"
ThisBuild / organization := "ai.dragonfly.code"
ThisBuild / scalacOptions ++= Seq("-feature", "-deprecation")

lazy val cliviz = crossProject(JSPlatform, JVMPlatform).settings(
  name := "cliviz",
  version := "0.0106.53",
  libraryDependencies ++= Seq(
    "ai.dragonfly.code" %%% "vector" % "0.53",
    "ai.dragonfly.code" %%% "democrossy" % "0.0105"
  )
).jvmSettings().jsSettings()

lazy val demo = crossProject(JSPlatform, JVMPlatform).dependsOn(cliviz).settings(
  name := "demo",
  Compile / mainClass := Some("Demo")
).jsSettings(
  Compile / fastOptJS / artifactPath := file("./demo/public_html/js/main.js"),
  Compile / fullOptJS / artifactPath := file("./demo/public_html/js/main.js"),
  scalaJSUseMainModuleInitializer := true
).jvmSettings()
