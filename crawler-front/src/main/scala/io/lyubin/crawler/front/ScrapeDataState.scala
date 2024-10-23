package io.lyubin.crawler.front

case class ScrapeDataState(
    useProxy: Boolean = false,
    proxyAddress: String = "",
    proxyPort: String = "",
    urls: String = "",
    showErrors: Boolean = false
) {
  def hasErrors: Boolean = (useProxy && (proxyAddress.isBlank || proxyPort.isBlank))

  def proxyError: Option[String] = {
    if (!proxyAddress.isBlank && useProxy) {
      None
    } else {
      Some("Proxy должен быть не пустым")
    }
  }

  def portError: Option[String] = {
    if (!proxyPort.isBlank && useProxy) {
      None
    } else {
      Some("Порт должен быть указан")
    }
  }

  def displayError(error: ScrapeDataState => Option[String]): Option[String] = {
    error(this).filter(_ => showErrors)
  }
}
