package org.cbi.subsinfo.api.auth

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.{Directives, Route}
import org.cbi.subsinfo.model.dao.User

trait RoleAuth extends Directives {

  sealed abstract case class Role(role: String)
  object ReloadRole extends Role("reload")
  object MetricRole extends Role("metric")
  object ClientRole extends Role("client")
  object UpdaterRole extends Role("updater")

  def withRoleAuth(role: Role, user: User)(f: => Route): Route =
    if (user.roles.contains(role.role))
      f
    else
      complete(Forbidden, s"You're don't have necessary role: ${role.role}")

  def withRoleAuth(roles: Set[Role], user: User)(f: Set[Role] => Route): Route = {

    val acceptedRoles = roles.filter(role => user.roles.contains(role.role))

    if (acceptedRoles.nonEmpty)
      f(acceptedRoles)
    else
      complete(Forbidden, s"You're don't have necessary one of roles: ${roles.mkString(", ")}")
  }
}
