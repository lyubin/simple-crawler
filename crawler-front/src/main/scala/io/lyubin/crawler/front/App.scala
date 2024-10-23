package io.lyubin.crawler.front

import com.raquo.laminar.api.L._
import org.scalajs.dom

object App {
  def main(args: Array[String]): Unit = {
    lazy val appContainer = dom.document.querySelector("#appContainer")
    val appElement = MainView()
    renderOnDomContentLoaded(appContainer, appElement)
  }
}
