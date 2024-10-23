package io.lyubin.crawler.front

import com.raquo.laminar.api.L._
import io.lyubin.crawler.shared.domain.{CrawlerError, CrawlerResult, RichResponse}

object ResultDataView {

  private def renderItem(item: Either[CrawlerError, CrawlerResult]) = {
    val (url, title, status) = item match {
      case Left(e: CrawlerError) => (e.url, e.cause, "")
      case Right(CrawlerResult(u, statusCode, titleOpt)) => (u.value, titleOpt.getOrElse(""), statusCode.value.toString)
    }
    tr(
      inContext(e => if (item.isLeft) cls("w3-red") else emptyMod),
      td(url),
      td(title),
      td(status),
    )
  }

  def apply(result: Var[Option[Either[Throwable, RichResponse]]]) = {
    div(
      cls("w3-row w3-margin-bottom"),
      table(
        cls("w3-table w3-border w3-bordered"),
        tr(
          cls("w3-light-grey"),
          td("Url"),
          td("Title"),
          td("Status")
        ),
        children <-- result.signal.map{
          case Some(Right(rr)) => rr.results.map(renderItem)
          case _ => Seq.empty
        }
      )
    )
  }
}
