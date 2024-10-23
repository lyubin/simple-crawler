package io.lyubin.crawler.shared

import io.circe._
import io.circe.generic.semiauto._

import java.net.URI
import scala.util.Try
import scala.util.control.NoStackTrace

object domain {

  case class Url(value: String) extends AnyVal
  object Url {
    def fromString(s: String): Either[Throwable, Url] = {
      Try(Url(new URI(s).toASCIIString)).toEither
    }
  }

  // статус http ответа (200 - ok, 4xx, 5xx)
  case class StatusCode(value: Int)
  object StatusCode {
    //noinspection TypeAnnotation
    val Ok = StatusCode(200)
  }


  // crawl result
  case class CrawlerResult(url: Url, statusCode: StatusCode, title: Option[String])

  case class Foo(a: Int, b: String, c: Boolean)
  implicit val fooDecoder: Decoder[Foo] = deriveDecoder[Foo]
  implicit val fooEncoder: Encoder[Foo] = deriveEncoder[Foo]

  // proxy
  case class ProxyServer(host: String, port: Int, typ: String)

  // crawler errors
  sealed trait CrawlerError extends NoStackTrace {
    def url: String
    def cause: String
  }
  case class MalformedUrlError(override val url: String) extends CrawlerError {
    override def cause: String = s"Malformed url: ${url}"
  }
  case class UnparsableDocumentError(override val url: String) extends CrawlerError {
    override def cause: String = s"Unparsable document error, source url: ${url}"
  }
  case class NetworkError(override val url: String) extends CrawlerError {
    override def cause: String = s"Network error while loading url: ${url}"
  }

  case class RichRequest(
      proxy: Option[ProxyServer],
      urls: Seq[String]
  )

  case class RichResponse(
      results: Seq[Either[CrawlerError, CrawlerResult]]
  )
}
