package org.cbi.subsinfo.api.route.resp

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import org.cbi.subsinfo.model.dto.SubscriberDto
import spray.json.DefaultJsonProtocol

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val subscriberDtoFormat = jsonFormat5(SubscriberDto)
}