package io.lyubin.crawler.shared

import io.lyubin.crawler.shared.domain._
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._

//noinspection TypeAnnotation
object Endpoints {

  import codecs._

  // простейший endpoint для 1 url, выводит только текст Url: String -> CrawlerError | String
  // GET http://localhost:8080/api/capture/title/flat?url=https://ya.ru
  val single = endpoint.get
    .in("api" / "capture" / "title" / "flat")
    .in(query[String]("url")) // может быть не валидным url, поэтому string
    .errorOut(jsonBody[CrawlerError])
    .out(stringBody)

  // endpoint для 1 url, выводит результат со статусом или ошибку Url: String -> CrawlerError | CrawlerResult
  // GET http://localhost:8080/api/capture/title?url=https://ya.ru
  val singleRich = endpoint.get
    .in("api" / "capture" / "title")
    .in(query[String]("url")) // может быть не валидным url, поэтому string
    .errorOut(jsonBody[CrawlerError])
    .out(jsonBody[CrawlerResult])

  // endpoint для получение title для нескольких элементов RichRequest -> RichResponse
  // POST http://localhost:8080/api/capture/title
  val multiRich = endpoint.post
    .in("api" / "capture" / "title")
    .in(jsonBody[RichRequest])
    .out(jsonBody[RichResponse])

  // на входе URL через запятую, на выходе CSV -> Url,Title,Status,Error
  // GET http://localhost:8080/api/capture/title/csv?urls=https://ya.ru,https://google.com
  val multiRawCSV = endpoint.get
    .in("api" / "capture" / "title" / "csv")
    .in(
      query[String]("urls")
        .map(s => s.split(",").map(u => Url(u)))(u => u.map(_.value).mkString(","))
    )
    .out(
      stringBody
        .map(
          _.split("\n").toList
        )(_.mkString("\n"))
    )
    .errorOut(stringBody)
}
