package io.lyubin.crawler.front

import com.raquo.laminar.api.L._

object InputDataView {
  def apply(state: Var[ScrapeDataState], loading: Var[Boolean]) = {
    val urlsWriter  = state.updater[String]((state, urls) => state.copy(urls = urls))
    div(
      disabled <-- loading.signal.map(!_),
      cls("w3-row w3-margin-bottom"),
      label(
        "Urls"
      ),
      textArea(
        "https://laminar.dev/documentation\nhttps://www.w3schools.com/w3css/w3css_tables.asp",
        cls("w3-input w3-border"),
        rows(10),
        //value --> urlsWriter,
        onChange.mapToValue --> urlsWriter,
      )
    )
  }
}
