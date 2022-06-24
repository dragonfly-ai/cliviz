ThisBuild / scalaVersion := "3.1.0"
ThisBuild / publishTo := Some( Resolver.file( "file",  new File("/var/www/maven" ) ) )
ThisBuild / resolvers += "ai.dragonfly.code" at "https://code.dragonfly.ai/"
ThisBuild / organization := "ai.dragonfly.code"
ThisBuild / scalacOptions ++= Seq("-feature", "-deprecation")

lazy val cliviz = crossProject(JSPlatform, JVMPlatform).settings(
  name := "cliviz",
  version := "0.01.527",
  libraryDependencies += "ai.dragonfly.code" %%% "vector" % "0.527"
).jvmSettings().jsSettings()

lazy val demo = crossProject(JSPlatform, JVMPlatform).dependsOn(cliviz).settings(
  name := "demo",
  Compile / mainClass := Some("Demo"),
//  libraryDependencies ++= Seq(
//    "ai.dragonfly.code" %%% "DemoCrossy" % "0.01"
//  )
).jsSettings(
  scalaJSUseMainModuleInitializer := true
).jvmSettings()
