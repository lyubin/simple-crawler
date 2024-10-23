package io.lyubin.crawler.front

import com.raquo.laminar.api.L._
import io.circe
import io.circe.parser._
import io.circe.syntax._
import io.lyubin.crawler.shared.domain.{CrawlerError, CrawlerResult, RichRequest, RichResponse, Url}

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

object MainView {

  import io.lyubin.crawler.shared.codecs._

  private val stateVar   = Var(ScrapeDataState())
  private val loadingVar = Var(false)
  private val richResult = Var(Option.empty[Either[Throwable, RichResponse]])
  private val isResultVisible = loadingVar.signal
    .combineWithFn(richResult.signal.map(_.isDefined)) { (loading, hasResult) => !loading && hasResult }

  def executeQuery(): EventStream[Either[Throwable, RichResponse]] = {
    if (stateVar.now().hasErrors) {
      stateVar.update(_.copy(showErrors = true))
      EventStream.empty
    } else {
      loadingVar.set(true)
      def toRichResponse(s: String): Either[circe.Error, RichResponse] = parse(s).flatMap(_.as[RichResponse])
      FetchStream
        .withDecoder[Either[Throwable, RichResponse]](r =>
          EventStream.fromFuture(r.text().toFuture.map(toRichResponse))
        )
        .post("api/capture/title", _.body(RichRequest(None, stateVar.now().urls.split("\n")).asJson.spaces2))
        .tapEach(_ => loadingVar.set(false))
        .recover { case err: Throwable => Some(Left(err)) }
    }
  }

  def apply(): HtmlElement = {
    div(
      div(
        cls("w3-container"),
        h1("Simple Crawler")
      ),
      div(
        cls("w3-container"),
        ProxyConfigView(stateVar),
        InputDataView(stateVar, loadingVar),
        child.maybe <-- loadingVar.signal.map(v => if (v) Some(h3("CRAWLING ....")) else None),
        child.maybe <-- isResultVisible.map(v => if (v) Some(ResultDataView(richResult)) else None),
        button(
          "Capture",
          disabled <-- loadingVar.signal,
          cls("w3-button w3-green w3-right"),
          onClick.flatMap(_ => executeQuery().map(e => Some(e))) --> richResult
        )
      )
    )
  }
}
