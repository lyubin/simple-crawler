package io.lyubin.crawler.core

import io.lyubin.crawler.shared.domain._

/** Главный сервис приложения
 */
trait SiteCrawler[F[_]] {

  /** Получить tilte страницы
   *
   * @param url URL страницы
   * @return Either[Ошибка, Результат]
   */
  def scrapeTitle(url: Url): F[Either[CrawlerError, CrawlerResult]]
}
