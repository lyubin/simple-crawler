package io.lyubin.crawler.server

import cats.effect._
import cats.implicits.toSemigroupKOps
import cats.syntax.all._
import io.lyubin.crawler.core.SiteCrawler
import io.lyubin.crawler.shared.Endpoints
import io.lyubin.crawler.shared.domain._
import org.http4s.HttpApp
import sttp.tapir._
import sttp.tapir.files.{staticResourceGetServerEndpoint, staticResourcesGetServerEndpoint}
import sttp.tapir.server.http4s.Http4sServerInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter

final class Http[F[_]: Async] private (crawler: SiteCrawler[F]) {

  private val serverInterpreter: Http4sServerInterpreter[F] = Http4sServerInterpreter[F]()

  private val docRoutes =
    Http4sServerInterpreter[F]().toRoutes(
      SwaggerInterpreter()
        .fromEndpoints[F](
          List(
            Endpoints.single,
            Endpoints.singleRich,
            Endpoints.multiRich,
            Endpoints.multiRawCSV
          ),
          "Simple Crawler Backend",
          "0.0.1"
        )
    )

//  private val scrapeSingleRoutes = serverInterpreter.toRoutes(
//    Endpoints.single.serverLogic(strUrl => {
//      Url
//        .fromString(strUrl)
//        .fold(
//          _   => MalformedUrlError(strUrl).asLeft[CrawlerResult].pure[F],
//          url => crawler.scrapeTitle(url)
//        )
//        .map(_.map(_.title.getOrElse("")))
//    })
//  )

  private val scrapeSingleRoutes = serverInterpreter.toRoutes(
    Endpoints.single.serverLogic(strUrl => {
      Url.fromString(strUrl).leftMap(_ => MalformedUrlError(strUrl)) match {
        case Right(url) =>
          crawler.scrapeTitle(url).map(_.map(_.title.getOrElse("")))
        case Left(err) =>
          val result: Either[CrawlerError, String] = err.asLeft[String]
          result.pure[F]
      }
    })
  )

//  private val scrapeSingleUrlRoutesRich = serverInterpreter.toRoutes(
//    Endpoints.singleRich.serverLogic(strUrl => {
//      Url
//        .fromString(strUrl)
//        .fold(
//          _ => {
//            val value: Either[CrawlerError, CrawlerResult] = MalformedUrlError(strUrl).asLeft[CrawlerResult]
//            value.pure[F]
//          },
//          url => crawler.scrapeTitle(url)
//        )
//    })
//  )

  private val scrapeSingleUrlRoutesRich = serverInterpreter.toRoutes(
    Endpoints.singleRich.serverLogic(strUrl => {
      Url.fromString(strUrl).leftMap(_ => MalformedUrlError(strUrl)) match {
        case Right(url) =>
          crawler.scrapeTitle(url)
        case Left(err) =>
          val result: Either[CrawlerError, CrawlerResult] = err.asLeft[CrawlerResult]
          result.pure[F]
      }
    })
  )

  private val scrapeMultiRich = serverInterpreter.toRoutes(
    Endpoints.multiRich
      .serverLogicSuccess[F](req =>
        req.urls
          .map(s => Url.fromString(s).leftMap(_ => MalformedUrlError(s)))
          .traverse {
            case Right(u)  => crawler.scrapeTitle(u)
            case Left(err) =>
              val result: Either[CrawlerError, CrawlerResult] = err.asLeft[CrawlerResult]
              result.pure[F]
          }
          .map(s => RichResponse(s))
      )
  )

  private val scrapeMultiRawCSV = serverInterpreter.toRoutes(
    Endpoints.multiRawCSV.serverLogic(urls =>
      urls.toList
        .traverse(u => crawler.scrapeTitle(u).map(r => (u.value, r)))
        .map(r =>
          r.map {
            case (u, Left(err)) => u + ",," + err.cause + ","
            case (u, Right(r))  => u + "," + r.title.getOrElse("") + "," + r.statusCode.value + ","
          }.asRight[CrawlerError]
            .leftMap(_.cause)
        )
        .map(r => r.map(l => "Url,Title,Status,Error" +: l))
    )
  )

  // SPA endpoints
  private val indexFileRoute = serverInterpreter.toRoutes(
    staticResourceGetServerEndpoint[F]("")(classOf[Http[F]].getClassLoader, "index.html")
  )
  private val devJSRoute = serverInterpreter.toRoutes(
    staticResourceGetServerEndpoint[F]("frontend" / "app.js")(classOf[Http[F]].getClassLoader, "dev.js")
  )
  private val assetsRoute = serverInterpreter.toRoutes(
    staticResourcesGetServerEndpoint[F]("assets")(classOf[Http[F]].getClassLoader, "assets/")
  )

  private val allRoutes = docRoutes <+> scrapeSingleRoutes <+>
    scrapeMultiRawCSV <+>
    scrapeSingleUrlRoutesRich <+>
    indexFileRoute <+> assetsRoute <+>
    devJSRoute <+> scrapeMultiRich

  val app: HttpApp[F] = allRoutes.orNotFound
}

object Http {
  def apply[F[_]: Async](crawler: SiteCrawler[F]): Http[F] = new Http[F](crawler)
}
