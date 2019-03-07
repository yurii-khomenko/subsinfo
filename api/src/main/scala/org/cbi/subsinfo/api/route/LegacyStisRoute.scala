package org.cbi.subsinfo.api.route

import org.cbi.subsinfo.api.auth.Auth
import org.cbi.subsinfo.api.route.resp.{Responses, SubscriberNotFound}
import org.cbi.subsinfo.model.dto.LegacyStisParams
import org.cbi.subsinfo.model.repo.SubscriberRepo

trait LegacyStisRoute extends Auth with SubscriberRepo with Responses {

  def legacyStisRoute = path("GET_SUBS_INFO") {

    get {
      parameters(
        'LOGIN.as[String],
        'PASSWORD.as[String],
        'CHANNEL.as[String],
        'SUBS.as[Long],
        'INFO.as[Byte]).as(LegacyStisParams) { params =>

        log.info(s"[${params.cid}] OK, get subsinfo request: $params")

        onSuccess(getSubscriber(params.subs)) {
          case Some(subscriber) => xmlRespOk(subscriber, params.cid)
          case None => xmlRespError(SubscriberNotFound, params.cid)
        }
      }
    }
  }
}