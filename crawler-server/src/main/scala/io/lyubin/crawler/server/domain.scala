package io.lyubin.crawler.server

import com.comcast.ip4s.{Host, Port}
import enumeratum.EnumEntry.Lowercase
import enumeratum.{CirisEnum, Enum, EnumEntry}

object domain {

  sealed abstract class AppEnvironment extends EnumEntry with Lowercase

  object AppEnvironment extends Enum[AppEnvironment] with CirisEnum[AppEnvironment] {
    case object Test extends AppEnvironment
    case object Prod extends AppEnvironment

    val values: IndexedSeq[AppEnvironment] = findValues
  }

  case class HttpServerConfig(
      host: Host,
      port: Port
  )

  case class AppConfig(
      httpServerConfig: HttpServerConfig
  )
}
