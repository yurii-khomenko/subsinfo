package org.cbi.subsinfo.api

import akka.http.scaladsl.model.headers.{BasicHttpCredentials, HttpChallenge}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.cbi.subsinfo.api.route.ApiRoute
import org.scalatest.{Matchers, WordSpec}

trait Config extends WordSpec with Matchers with ScalatestRouteTest with ApiRoute {

  val responseWWWAuthHeader = HttpChallenge("Basic", Some("stis2"), Map("charset" → "UTF-8"))
  val methodNotAllowed = "HTTP method not allowed, supported methods: GET, HEAD"
  val requiresAuth = "The resource requires authentication, which was not supplied with the request"
  val invalidAuth = "The supplied authentication is invalid"
  val invalidRole = "You're don't have necessary role: "

  val emptyCredentials = BasicHttpCredentials("", "")
  val invalidCredentials = BasicHttpCredentials("Peter", "pan")
  val validClientCredentials = BasicHttpCredentials("test-client", "test-client-password")
  val validUpdaterCredentials = BasicHttpCredentials("test-updater", "test-updater-password")

  val msisdn = 380670000001L
  val snInBlackList = 777
  val snOutBlackList = 5675
}