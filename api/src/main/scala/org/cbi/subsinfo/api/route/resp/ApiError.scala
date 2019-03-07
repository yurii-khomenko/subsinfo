package org.cbi.subsinfo.api.route.resp

sealed abstract class ApiError(val errorId: Int, val errorMsg: String)

case object SubscriberNotFound extends ApiError(1, "Subscriber not found")