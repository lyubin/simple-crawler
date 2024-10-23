import Dependencies.*

lazy val scala212               = "2.12.20"
lazy val scala213               = "2.13.15"
lazy val scala3                 = "3.5.1"
lazy val supportedScalaVersions = List(scala212, scala213, scala3)

ThisBuild / scalaVersion     := scala213
ThisBuild / version          := "0.0.1"
ThisBuild / organization     := "io.lyubin.crawler"
ThisBuild / organizationName := "AcmeDev"

def prj(id: String) = Project(s"crawler-${id}", file(s"crawler-${id}"))

lazy val root = Project(id = "crawler", base = file("."))
  .aggregate(core, server, frontend, shared.js, shared.jvm)

val commonSettings = Seq()

lazy val core = prj("core")
  .settings(commonSettings *)
  .settings(
    testFrameworks += new TestFramework("weaver.framework.CatsEffect"),
    libraryDependencies ++= Seq(
      CompilerPlugin.kindProjector,
      CompilerPlugin.semanticDB,
      Libraries.htmlUnit,
      Libraries.cats,
      Libraries.catsEffect,
      Libraries.fs2,
      Libraries.log4cats,
      Libraries.log4catsSlf4j,
      Libraries.logback           % Runtime,
      Libraries.scalaTest         % Test,
      Libraries.scalaTic          % Test,
      Libraries.weaverCats        % Test,
      Libraries.catsEffectTesting % Test
    )
  )
  .dependsOn(shared.jvm)

lazy val server = prj("server")
  .settings(commonSettings *)
  .settings(
    testFrameworks += new TestFramework("weaver.framework.CatsEffect"),
    libraryDependencies ++= Seq(
      CompilerPlugin.kindProjector,
      CompilerPlugin.semanticDB,
      Libraries.cirisCore,
      Libraries.cirisEnum,
      Libraries.cirisRefined,
      // Libraries.cirisHttp4s,
      Libraries.http4sCore,
      Libraries.http4sDsl,
      Libraries.http4sCirce,
      Libraries.http4sServer,
      Libraries.sttpCore,
      Libraries.tapirCore,
      Libraries.tapirJsonCirce,
      Libraries.tapirSwaggerUI,
      Libraries.tapirHttp4s,
      Libraries.tapirFiles,
      Libraries.log4cats,
      Libraries.log4catsSlf4j,
      Libraries.logback           % Runtime,
      Libraries.scalaTest         % Test,
      Libraries.scalaTic          % Test,
      Libraries.catsEffectTesting % Test
    )
  )
  .dependsOn(core, shared.jvm)

lazy val a = crossProject(JSPlatform, JVMPlatform)

lazy val shared = crossProject(JSPlatform, JVMPlatform)
  .in(file(s"crawler-shared"))
  .settings(commonSettings *)
  .settings(
    testFrameworks += new TestFramework("weaver.framework.CatsEffect"),
    libraryDependencies ++= Seq(
      CompilerPlugin.kindProjector,
      CompilerPlugin.semanticDB,
      "com.softwaremill.sttp.tapir"   %%% "tapir-core"        % V.tapir,
      "com.softwaremill.sttp.tapir"   %%% "tapir-json-circe"  % V.tapir,
      "com.softwaremill.sttp.tapir"   %%% "tapir-sttp-client" % V.tapir,
      "com.softwaremill.sttp.client4" %%% "core"              % V.sttp,
      "com.softwaremill.sttp.client4" %%% "fs2"               % V.sttp,
      Libraries.scalaTest               % Test,
      Libraries.scalaTic                % Test,
      Libraries.catsEffectTesting       % Test
    )
  )
//.dependsOn(core)

lazy val frontend = prj("front")
  .settings(commonSettings *)
  .enablePlugins(ScalaJSPlugin)
  .settings(
    scalaJSUseMainModuleInitializer := true,
    testFrameworks += new TestFramework("weaver.framework.CatsEffect"),
    libraryDependencies ++=
      Seq(
        "org.typelevel"               %%% "cats-core"         % V.cats,
        "org.typelevel"               %%% "cats-effect"       % V.catsEffect,
        "co.fs2"                      %%% "fs2-core"          % V.fs2,
        "org.http4s"                  %%% "http4s-core"       % V.http4s,
        "org.http4s"                  %%% "http4s-client"     % V.http4s,
        "org.http4s"                  %%% "http4s-circe"      % V.http4s,
        "org.http4s"                  %%% "http4s-dom"        % V.http4sDom,
        "com.softwaremill.sttp.tapir" %%% "tapir-sttp-client" % V.tapir,
        "com.softwaremill.sttp.tapir" %%% "tapir-core"        % V.tapir,
        "com.raquo"                   %%% "laminar"           % V.laminar
      ),
    libraryDependencies ++= Seq(
      CompilerPlugin.kindProjector,
      CompilerPlugin.semanticDB,
      Libraries.logback           % Runtime,
      Libraries.scalaTest         % Test,
      Libraries.scalaTic          % Test,
      Libraries.catsEffectTesting % Test
    )
  )
  .dependsOn(shared.js)

val jsPath = "crawler-server/src/main/resources"

lazy val fastOptCompileCopy = taskKey[Unit]("")
fastOptCompileCopy := {
  val source = (frontend / Compile / fastOptJS).value.data
  IO.copyFile(
    source,
    baseDirectory.value / jsPath / "dev.js"
  )
}

lazy val fullOptCompileCopy = taskKey[Unit]("")
fullOptCompileCopy := {
  val source = (frontend / Compile / fullOptJS).value.data
  IO.copyFile(
    source,
    baseDirectory.value / jsPath / "prod.js"
  )
}

addCommandAlias("runDev", ";fastOptCompileCopy; crawler-server/reStart --mode dev")
addCommandAlias("runProd", ";fullOptCompileCopy; crawler-server/reStart --mode prod")
