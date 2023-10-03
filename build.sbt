val appVersion:String = "0.103"
val globalScalaVersion = "3.3.0"

ThisBuild / organization := "ai.dragonfly"
ThisBuild / organizationName := "dragonfly.ai"
ThisBuild / resolvers := Resolver.sonatypeOssRepos("releases")
ThisBuild / startYear := Some(2023)
ThisBuild / licenses := Seq(License.Apache2)
ThisBuild / developers := List( tlGitHubDev("dragonfly-ai", "dragonfly.ai") )
ThisBuild / scalaVersion := globalScalaVersion

ThisBuild / tlBaseVersion := appVersion
ThisBuild / tlCiReleaseBranches := Seq()
ThisBuild / tlSonatypeUseLegacyHost := false

ThisBuild / nativeConfig ~= {
  _.withLTO(scala.scalanative.build.LTO.thin)
    .withMode(scala.scalanative.build.Mode.releaseFast)
    .withGC(scala.scalanative.build.GC.commix)
}
lazy val cliviz = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .crossType(CrossType.Full)
  .settings(
    libraryDependencies ++= Seq(
      "ai.dragonfly" %%% "slash" % "0.1",
    )
  ).jvmSettings().jsSettings()

lazy val demo = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .crossType(CrossType.Full)
  .enablePlugins(NoPublishPlugin)
  .dependsOn(cliviz)
  .settings(
    libraryDependencies ++= Seq(
      "ai.dragonfly" %%% "democrossy" % "0.102"
    ),
    name := "demo",
    Compile / mainClass := Some("Demo")
  ).jsSettings(
    Compile / fullOptJS / artifactPath := file("./docs/js/main.js"),
    scalaJSUseMainModuleInitializer := true
  ).jvmSettings()


lazy val root = tlCrossRootProject.aggregate(cliviz).settings(name := "cliviz")

lazy val docs = project.in(file("site")).enablePlugins(TypelevelSitePlugin).settings(
  mdocVariables := Map(
    "VERSION" -> appVersion,
    "SCALA_VERSION" -> globalScalaVersion
  ),
  laikaConfig ~= { _.withRawContent }
)

lazy val unidocs = project
  .in(file("unidocs"))
  .enablePlugins(TypelevelUnidocPlugin) // also enables the ScalaUnidocPlugin
  .settings(
    name := "cliviz-docs",
    ScalaUnidoc / unidoc / unidocProjectFilter := inProjects(cliviz.jvm, cliviz.js, cliviz.native)
  )