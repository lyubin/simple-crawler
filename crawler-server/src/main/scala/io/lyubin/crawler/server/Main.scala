package io.lyubin.crawler.server

import cats.effect.{ExitCode, IO, IOApp}
import io.lyubin.crawler.core.HtmlUnitCrawler
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Server
import org.http4s.server.defaults.Banner
import org.typelevel.log4cats.{Logger, SelfAwareStructuredLogger}
import org.typelevel.log4cats.slf4j.Slf4jLogger

object Main extends IOApp {

  implicit private val logger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  private def showEmberBanner[F[_]: Logger](s: Server): F[Unit] =
    Logger[F].info(s"\n${Banner.mkString("\n")}\nHTTP Server started at ${s.address}")

  override def run(args: List[String]): IO[ExitCode] = {
    for {
      cfg <- Config.load[IO]
      crawler = HtmlUnitCrawler[IO]
      http    = Http[IO](crawler)
      _ <- EmberServerBuilder
            .default[IO]
            .withHost(cfg.httpServerConfig.host)
            .withPort(cfg.httpServerConfig.port)
            .withHttpApp(http.app)
            .build
            .evalTap(showEmberBanner[IO])
            .useForever
    } yield ExitCode.Success
  }
}
