package org.cbi.subsinfo.api.route

import java.util.UUID

import org.cbi.subsinfo.api.auth.Auth
import org.cbi.subsinfo.api.route.resp.{Responses, SubscriberNotFound}
import org.cbi.subsinfo.model.dto.SubscriberDto
import org.cbi.subsinfo.model.repo.SubscriberRepo

trait SubscribersRoute extends Auth with SubscriberRepo with Responses {

  def subscriberRoute = path("api" / "subscribers" / LongNumber) { msisdn =>

    get {
      withAuth(ClientRole) {

        val cid = UUID.randomUUID

        log.info(s"[$cid] OK, get subscriber request, msisdn: $msisdn")

        onSuccess(getSubscriber(msisdn)) {
          case Some(subscriber) => jsonRespEntity(subscriber, cid)
          case None => jsonRespError(SubscriberNotFound, cid)
        }
      }
    } ~
    put {
      withAuth(UpdaterRole) {

        entity(as[SubscriberDto]) { dto =>

          val cid = UUID.randomUUID

          log.info(s"[$cid] OK, update subscriber request, msisdn: $msisdn")

          onSuccess(saveSubscriber(msisdn, dto)) { _ =>
            jsonRespOk(cid)
          }
        }
      }
    }
  }
}