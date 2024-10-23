package io.lyubin.crawler.core

import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import io.lyubin.crawler.shared.domain._
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger

// Конечно тестировать на живых сайтах не лучший подход.
// По хорошему нужно в beforeAll/afterAll поднимать сервер
class HtmlUnitCrawlerSpec extends AsyncFlatSpec with AsyncIOSpec with Matchers {

  private implicit val logger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  behavior of "HtmlUnitCrawler"

  it should "scraper tile in arbitrary wikipedia page" in {
    val url = Url("https://ru.wikipedia.org/wiki/%D0%91%D0%BB%D0%B5%D0%B9%D0%BA,_%D0%A3%D0%B8%D0%BB%D1%8C%D1%8F%D0%BC")
    HtmlUnitCrawler[IO]
      .scrapeTitle(url)
      .asserting { resOrErr =>
        resOrErr shouldBe Symbol("right")
        val Right(r) = resOrErr
        r.title shouldBe defined
        r.url shouldBe url
        r.statusCode shouldBe StatusCode.Ok
      }
  }

  it should "raise UnparsableDocumentError on invalid document type" in {
    val url = Url(
      "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b6/William_Blake%2C_Self_Portrait%2C_1802%2C_Monochrome_Wash.jpg/250px-William_Blake%2C_Self_Portrait%2C_1802%2C_Monochrome_Wash.jpg"
    )
    HtmlUnitCrawler[IO]
      .scrapeTitle(url)
      .asserting { resOrErr =>
        resOrErr shouldBe Symbol("left")
        val Left(UnparsableDocumentError(u)) = resOrErr
        Url(u) shouldBe url
      }
  }

  it should "raise NetworkError on invalid server address" in {
    val url = Url("http://impossible_server_name.zzz111")
    HtmlUnitCrawler[IO]
      .scrapeTitle(url)
      .asserting { resOrErr =>
        resOrErr shouldBe Symbol("left")
        val Left(NetworkError(u)) = resOrErr
        Url(u) shouldBe url
      }
  }
}
