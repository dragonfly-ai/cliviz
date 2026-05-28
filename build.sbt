
val appVersion:String = "1.0"
val globalScalaVersion = "3.3.7"

ThisBuild / organization := "ai.dragonfly"
ThisBuild / organizationName := "dragonfly.ai"
ThisBuild / startYear := Some(2023)
ThisBuild / scalaVersion := globalScalaVersion

ThisBuild / tlBaseVersion := appVersion
ThisBuild / tlCiReleaseBranches := Seq()

ThisBuild / nativeConfig ~= {
  _.withLTO(scala.scalanative.build.LTO.thin)
    .withMode(scala.scalanative.build.Mode.releaseFast)
    .withGC(scala.scalanative.build.GC.commix)
}

lazy val cliviz = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .crossType(CrossType.Full)
  .settings(
    libraryDependencies ++= Seq(
      "ai.dragonfly" %%% "slash" % "0.4.5",
    )
  ).jvmSettings().jsSettings()

lazy val demo = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .crossType(CrossType.Full)
  .enablePlugins(NoPublishPlugin)
  .dependsOn(cliviz)
  .settings(
    name := "demo",
    Compile / mainClass := Some("Demo")
  ).jsSettings(
    scalaJSUseMainModuleInitializer := true
  ).jvmSettings()


lazy val root = tlCrossRootProject.aggregate(cliviz).settings(name := "cliviz")

lazy val unidocs = project
  .in(file("unidocs"))
  .enablePlugins(TypelevelUnidocPlugin) // also enables the ScalaUnidocPlugin
  .settings(
    name := "cliviz-docs",
    ScalaUnidoc / unidoc / unidocProjectFilter := inProjects(cliviz.jvm, cliviz.js, cliviz.native)
  )