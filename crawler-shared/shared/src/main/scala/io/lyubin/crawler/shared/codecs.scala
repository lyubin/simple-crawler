package io.lyubin.crawler.shared

import io.circe.{Codec, Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.lyubin.crawler.shared.domain._

import scala.util.Try

object codecs {

  implicit val urlEncoder: Encoder[Url] = Encoder[String].contramap[Url](u => u.value)
  implicit val urlDecoder: Decoder[Url] = Decoder[String].emap(s => Url.fromString(s).left.map(e => e.toString))

  implicit val statusCodeEncoder: Encoder[StatusCode] = Encoder[String].contramap[StatusCode](u => u.value.toString)
  implicit val statusCodeDecoder: Decoder[StatusCode] = Decoder[String].emap[StatusCode](s => Try(StatusCode(s.toInt)).toEither.left.map(e => e.toString))

  implicit val crawlerResultEncoder: Encoder[CrawlerResult] = deriveEncoder[CrawlerResult]
  implicit val crawlerResultDecoder: Decoder[CrawlerResult] = deriveDecoder[CrawlerResult]

  implicit val crawlerErrorEncoder: Encoder[CrawlerError] = deriveEncoder[CrawlerError]
  implicit val crawlerErrorDecoder: Decoder[CrawlerError] = deriveDecoder[CrawlerError]

  implicit val proxyEncoder: Encoder[ProxyServer] = deriveEncoder[ProxyServer]
  implicit val proxyDecoder: Decoder[ProxyServer] = deriveDecoder[ProxyServer]

  implicit val richRequestEncoder: Encoder[RichRequest] = deriveEncoder[RichRequest].mapJson(_.dropNullValues.dropEmptyValues)
  implicit val richRequestDecoder: Decoder[RichRequest] = deriveDecoder[RichRequest]

  implicit val crawlerErrorOrResultEncoder: Encoder[Either[CrawlerError, CrawlerResult]] = deriveEncoder[Either[CrawlerError, CrawlerResult]]
  implicit val crawlerErrorOrResultDecoder: Decoder[Either[CrawlerError, CrawlerResult]] = deriveDecoder[Either[CrawlerError, CrawlerResult]]

  implicit val richResponseEncoder: Encoder[RichResponse] = deriveEncoder[RichResponse]
  implicit val richResponseDecoder: Decoder[RichResponse] = deriveDecoder[RichResponse]

}
