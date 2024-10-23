package io.lyubin.crawler.front

import com.raquo.laminar.api.L._
import io.lyubin.crawler.front.MainView.stateVar

object ProxyConfigView {

  def apply(state: Var[ScrapeDataState]) = {

    val useProxyWriter     = state.updater[Boolean]((state, useProxy) => state.copy(useProxy = useProxy, showErrors = false))
    val proxyAddressWriter = state.updater[String]((state, proxyAddress) => state.copy(proxyAddress = proxyAddress))
    val proxyPortWriter    = state.updater[String]((state, proxyPort) => state.copy(proxyPort = proxyPort))

    def renderInputRow(error: ScrapeDataState => Option[String])(mods: Modifier[HtmlElement]*): HtmlElement = {
      val errorSignal = state.signal.map(_.displayError(error))
      div(
        mods,
        child.maybe <-- errorSignal.map(_.map(err => div(cls("w3-red"), err)))
      )
    }

    div(
      div(
        cls("w3-row"),
        div(
          cls("w3-third"),
          input(
            cls("w3-check"),
            typ("checkbox"),
            value("false"),
            checked(false),
            onInput.mapToChecked --> useProxyWriter
          ),
          label("Use Proxy")
        )
      ),
      div(
        cls("w3-row w3-margin-bottom"),
        renderInputRow(_.proxyError)(
          cls("w3-third"),
          styleAttr("padding-right: 1em"),
          label(
            "Proxy Address",
            cls("w3-text-grey")
          ),
          input(
            cls("w3-input w3-border"),
            typ("text"),
            value("http://11.22.33.44"),
            disabled <-- state.signal.map(_.useProxy).map(!_),
            controlled(
              value <-- state.signal.map(_.proxyAddress),
              onInput.mapToValue --> proxyAddressWriter
            )
          )
        ),
        renderInputRow(_.portError)(
          cls("w3-third"),
          styleAttr("padding-right: 1em"),
          label(
            "Proxy Port",
            cls("w3-text-grey")
          ),
          input(
            cls("w3-input w3-border"),
            typ("text"),
            value("1234"),
            disabled <-- state.signal.map(_.useProxy).map(!_),
            controlled(
              value <-- state.signal.map(_.proxyPort),
              onInput.mapToValue.filter(_.forall(Character.isDigit)) --> proxyPortWriter
            )
          )
        ),
        div(
          cls("w3-third"),
          label(
            "Protocol",
            cls("w3-text-grey"),
            select(
              cls("w3-select w3-border"),
              option("Socks"),
              option("Http"),
              option("Https"),
              disabled <-- state.signal.map(_.useProxy).map(!_),
            )
          )
        )
      )
    )
  }
}
