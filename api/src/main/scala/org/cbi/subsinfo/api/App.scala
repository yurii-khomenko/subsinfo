package org.cbi.subsinfo.api

import akka.http.scaladsl.server.HttpApp
import org.cbi.subsinfo.api.route.ApiRoute

object App extends HttpApp with ApiRoute {

  def main(args: Array[String]): Unit =
    startServer("0.0.0.0", 8069)
}
