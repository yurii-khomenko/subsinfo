package org.cbi.subsinfo.api.auth

import akka.http.scaladsl.server.directives.Credentials
import akka.http.scaladsl.server.{Directives, Route}
import org.cbi.subsinfo.model.dao.User
import org.cbi.subsinfo.model.repo.DB

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait BasicAuth extends Directives with DB {

  def users: Map[String, User]

  private def userPassAuthenticator(credentials: Credentials): Future[Option[User]] = credentials match {
    case p@Credentials.Provided(userId) => Future {
      users.get(userId).filter(user => p.verify(user.password))
    }
    case _ => Future.successful(None)
  }

  def withBasicAuth(f: User => Route): Route =
    authenticateBasicAsync(realm = "stis2", userPassAuthenticator) { user =>
      f(user)
    }
}
