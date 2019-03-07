package org.cbi.subsinfo.api.route.resp

import java.text.SimpleDateFormat
import java.util.UUID

import akka.http.scaladsl.model.MediaTypes.`application/json`
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.headers.`Content-Type`
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse}
import akka.http.scaladsl.server.{Directives, Route}
import com.fasterxml.jackson.databind.ObjectMapper
import org.cbi.subsinfo.model.dao.Subscriber
import org.slf4j.LoggerFactory.getLogger

trait Responses extends Directives with JsonSupport {

  val log = getLogger(getClass)
  val mapper = new ObjectMapper

  def jsonRespOk(cid: UUID): Route = {

    val body = s"""{"cid":"$cid"}"""

    log.info(s"[$cid] OK, request processed successfully, http code: ${OK.intValue}, body: $body")
    complete(HttpResponse(OK, entity = HttpEntity(`application/json`, body)))
  }

  def jsonRespEntity(subscriber: Subscriber, cid: UUID): Route = {

    val body = mapper.writeValueAsString(ResponseDto(cid, subscriber))

    log.info(s"[$cid] OK, request processed successfully, http code: ${OK.intValue}, body: $body")

    complete(HttpResponse(OK, entity = HttpEntity(`application/json`, body)))
  }

  def jsonRespError(error: ApiError, cid: UUID): Route = {

    val body = s"""{"cid":"$cid","errorId":${error.errorId},"errorMsg":"${error.errorMsg}"}"""

    error match {
      case SubscriberNotFound =>

        log.info(s"[$cid] FAIL, subscriber not found, http code: ${NotFound.intValue}, response: $body")

        complete(HttpResponse(NotFound, entity = HttpEntity(`application/json`, body)))
    }
  }

  val langTypeToLangStr = Map(
    1 -> "en_latin",
    2 -> "en_cyrillic",
    3 -> "ru_latin",
    4 -> "ru_cyrillic",
    5 -> "ua_latin",
    6 -> "ua_cyrillic"
  )

  val langStrToLangType = Map(
    "en_latin" -> 1,
    "en_cyrillic" -> 2,
    "ru_latin" -> 3,
    "ru_cyrillic" -> 4,
    "ua_latin" -> 5,
    "ua_cyrillic" -> 6
  )

  val sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss")

  def xmlRespOk(subscriber: Subscriber, cid: UUID): Route = {

    val body =
      s"<SELFCARE>\n<GET_SUBS_INFO>" +
        s"<TYPE>${subscriber.subscriberType}</TYPE>" +
        s"<SEGMENT>${subscriber.segmentType}</SEGMENT>" +
        s"<CHANGE_DATE>${sdf.format(subscriber.changeDate)}<CHANGE_DATE/>" +
        s"<HLR>${subscriber.hlrType}</HLR>" +
        s"<LANG>${langTypeToLangStr.getOrElse(subscriber.languageType, "")}</LANG>" +
        s"\n</GET_SUBS_INFO>\n</SELFCARE>"

    log.info(s"[$cid] OK, request processed successfully, http code: ${OK.intValue}, body: $body")

    complete(OK, List(`Content-Type`(ContentTypes.`text/xml(UTF-8)`)), body)
  }

  def xmlRespError(error: ApiError, cid: UUID): Route = {

    val body =
      s"<SELFCARE>\n<GET_SUBS_INFO>" +
        s"<ERROR>" +
        s"<ERROR_ID>${error.errorId}</ERROR_ID>" +
        s"<ERROR_MESSAGE>${error.errorMsg}</ERROR_MESSAGE>" +
        s"</ERROR>" +
        s"\n</GET_SUBS_INFO>\n</SELFCARE>"

    log.info(s"[$cid] OK, request processed successfully, http code: ${OK.intValue}, body: $body")

    complete(OK, List(`Content-Type`(ContentTypes.`text/xml(UTF-8)`)), body)
  }
}
