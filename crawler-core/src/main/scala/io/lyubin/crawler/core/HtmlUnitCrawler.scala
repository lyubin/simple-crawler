package io.lyubin.crawler.core

import cats.ApplicativeThrow
import cats.effect.Sync
import cats.syntax.all._
import io.lyubin.crawler.shared.domain._
import org.htmlunit.html.{DomNode, HtmlPage}
import org.htmlunit.{Page, WebClient}
import org.typelevel.log4cats.Logger

import java.io.IOException
import scala.jdk.CollectionConverters._

object HtmlUnitCrawler {
  def apply[F[_]: Sync: ApplicativeThrow: Logger]: SiteCrawler[F] = new SiteCrawler[F] {

    private def loadPage(url: Url): F[HtmlPage] =
      Sync[F]
        .delay {
          val wc = new WebClient()
          wc.getOptions.setJavaScriptEnabled(false)
          wc.getOptions.setCssEnabled(false)
          wc.getOptions.setThrowExceptionOnScriptError(false)
          wc.getOptions.setThrowExceptionOnScriptError(false)
          wc.getPage[Page](url.value)
        }
        .flatMap {
          case p: HtmlPage => p.pure[F]
          case _           => UnparsableDocumentError(url.value).raiseError[F, HtmlPage]
        }
        .adaptError { case _: IOException =>
          NetworkError(url.value)
        }

    private def getTitleFromPage(doc: HtmlPage): F[Option[String]] = Sync[F].delay {
      doc
        .getByXPath[DomNode]("//title")
        .asScala
        .map(_.asNormalizedText())
        .headOption
    }

    override def scrapeTitle(url: Url): F[Either[CrawlerError, CrawlerResult]] = {
      val result: F[CrawlerResult] = for {
        _     <- Logger[F].info(s"Load page: ${url.value}")
        doc   <- loadPage(url)
        title <- getTitleFromPage(doc)
        _     <- Logger[F].info(s"Title captured: ${title} for url: ${url.value}")
      } yield CrawlerResult(url, StatusCode(doc.getWebResponse.getStatusCode), title)

      result.attemptNarrow[CrawlerError]
    }
  }
}
