package io.lyubin.crawler.server

import cats.effect.Async
import cats.syntax.all._
import ciris._
import com.comcast.ip4s._
import io.lyubin.crawler.server.domain.AppEnvironment._
import io.lyubin.crawler.server.domain._

object Config {

  // ciris-http4s не совместим с последними релизами http4s, поэтому руками кодеки приходится делать
  implicit private val hostDecoder: ConfigDecoder[String, Host] = ConfigDecoder[String, String]
    .mapOption[Host]("Host")(Host.fromString)
  implicit private val portDecoder: ConfigDecoder[String, Port] = ConfigDecoder[String, String]
    .mapOption[Port]("Port")(Port.fromString)

  def load[F[_]: Async]: F[AppConfig] = {
    env("CRAWLER_APP_ENV")
      .default("prod")
      .as[AppEnvironment]
      .flatMap {
        case Test =>
          defaultProd[F]
        case Prod =>
          defaultTest[F]
      }
      .load[F]
  }

  private def defaultProd[F[_]]: ConfigValue[F, AppConfig] =
    (
      env("SERVER_HOST").as[Host].default(host"0.0.0.0"),
      env("SERVER_PORT").as[Port].default(port"8080") // mem:test
    ).parMapN { (host, port) =>
      AppConfig(
        HttpServerConfig(host"0.0.0.0", port"8080")
      )
    }

  private def defaultTest[F[_]]: ConfigValue[F, AppConfig] =
    (
      env("SERVER_HOST").as[Host].default(host"0.0.0.0"),
      env("SERVER_PORT").as[Port].default(port"8080") // mem:test
    ).parMapN { (host, port) =>
      AppConfig(
        HttpServerConfig(host"0.0.0.0", port"8080")
      )
    }
}
