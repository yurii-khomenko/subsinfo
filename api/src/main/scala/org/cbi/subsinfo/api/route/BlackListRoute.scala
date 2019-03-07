package org.cbi.subsinfo.api.route

import akka.http.scaladsl.model.MediaTypes.`application/json`
import akka.http.scaladsl.model._
import org.cbi.subsinfo.api.auth.Auth
import org.cbi.subsinfo.model.repo.DB

trait BlackListRoute extends Auth with DB {

  def blackListRoute = path("api" / "pass") {

    get {

      withAuth(ClientRole) {

        parameters('msisdn.as[Long], 'sn) { (msisdn, sn) =>

          val pass = Option(blackListRuleAccessor.get(msisdn).one)
            .forall(result => !result.shortNumbers.contains(sn))

          complete(HttpEntity(`application/json`, s"""{"passed":$pass}"""))
        }
      }
    }
  }
}