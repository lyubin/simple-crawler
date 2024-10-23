import sbt.Keys.*
import sbt.*
import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._

object Dependencies {

  object V {
    val cats       = "2.12.0"
    val catsEffect = "3.5.4"
    val circe      = "0.14.6"
    val ciris      = "2.3.2"
    val log4cats   = "2.7.0"
    val newtype    = "0.4.4"
    val refined    = "0.11.2"

    val kindProjector   = "0.13.3"
    val logback         = "1.5.9"
    val scalaLogging    = "3.9.5"
    val organizeImports = "0.6.0"
    val semanticDB      = "4.10.1"

    val weaver = "0.8.4"

    val scalaTest         = "3.2.19"
    val zioHttp           = "3.0.1"
    val zio               = "2.1.9"
    val necko             = "4.4.0"
    val catsEffectTesting = "1.5.0"
    val http4s            = "0.23.28"
    val http4sDom         = "0.2.11"
    val tapir             = "1.11.7"
    val laminar           = "17.1.0"
    val sttp              = "4.0.0-M19"
    val fs2               = "3.11.0"
  }

  object Libraries {
    def circe(artifact: String): ModuleID  = "io.circe"                    %% s"circe-$artifact"  % V.circe
    def ciris(artifact: String): ModuleID  = "is.cir"                      %% artifact            % V.ciris
    def http4s(artifact: String): ModuleID = "org.http4s"                  %% s"http4s-$artifact" % V.http4s
    def tapir(artifact: String): ModuleID  = "com.softwaremill.sttp.tapir" %% s"tapir-$artifact"  % V.tapir

    val cats       = "org.typelevel" %% "cats-core"   % V.cats
    val catsEffect = "org.typelevel" %% "cats-effect" % V.catsEffect
    val fs2        = "co.fs2"        %% "fs2-core"    % V.fs2

    val circeCore    = circe("core")
    val circeGeneric = circe("generic")
    val circeParser  = circe("parser")
    val circeRefined = circe("refined")

    val cirisCore    = ciris("ciris")
    val cirisRefined = ciris("ciris-refined")
    // val cirisHttp4s = ciris("ciris-http4s")
    val cirisEnum = ciris("ciris-enumeratum")

    // http4s
    val http4sCore   = http4s("core")
    val http4sDsl    = http4s("dsl")
    val http4sCirce  = http4s("circe")
    val http4sServer = http4s("ember-server")
    val http4sClient = http4s("ember-client")

    // tapir
    val tapirCore      = tapir("core")
    val tapirJsonCirce = tapir("json-circe")
    val tapirSwaggerUI = tapir("swagger-ui-bundle")
    val tapirHttp4s    = tapir("http4s-server")
    val tapirFiles     = tapir("files")
    val sttpTapir      = "com.softwaremill.sttp.tapir"   %% "tapir-http4s-client" % V.tapir
    val sttpCore       = "com.softwaremill.sttp.client4" %% "core"                % V.sttp

    val htmlUnit = "org.htmlunit" % "htmlunit" % V.necko

    val refinedCore = "eu.timepit" %% "refined"      % V.refined
    val refinedCats = "eu.timepit" %% "refined-cats" % V.refined

    val log4cats      = "org.typelevel" %% "log4cats-slf4j" % V.log4cats
    val log4catsSlf4j = "org.typelevel" %% "log4cats-slf4j" % V.log4cats

    val newtype = "io.estatico" %% "newtype" % V.newtype

    // Logging
    val logback = "ch.qos.logback" % "logback-classic" % V.logback

    // Test
    val catsLaws          = "org.typelevel"       %% "cats-laws"                     % V.cats
    val log4catsNoOp      = "org.typelevel"       %% "log4cats-noop"                 % V.log4cats
    val refinedScalacheck = "eu.timepit"          %% "refined-scalacheck"            % V.refined
    val catsEffectTesting = "org.typelevel"       %% "cats-effect-testing-scalatest" % V.catsEffectTesting
    val weaverCats        = "com.disneystreaming" %% "weaver-cats"                   % V.weaver
    val weaverDiscipline  = "com.disneystreaming" %% "weaver-discipline"             % V.weaver
    val weaverScalaCheck  = "com.disneystreaming" %% "weaver-scalacheck"             % V.weaver

    val scalaTest = "org.scalatest" %% "scalatest" % V.scalaTest
    val scalaTic  = "org.scalactic" %% "scalactic" % V.scalaTest
  }

  object CompilerPlugin {
    val kindProjector = compilerPlugin(
      "org.typelevel" % "kind-projector" % V.kindProjector cross CrossVersion.full
    )
    val semanticDB = compilerPlugin(
      "org.scalameta" % "semanticdb-scalac" % V.semanticDB cross CrossVersion.full
    )
  }
}
