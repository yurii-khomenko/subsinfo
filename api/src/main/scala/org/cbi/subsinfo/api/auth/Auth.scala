package org.cbi.subsinfo.api.auth

import akka.http.scaladsl.server.Route
import org.cbi.subsinfo.model.dao.User

trait Auth extends BasicAuth with IpAuth with RoleAuth {

  def withBasicAuthIp(f: (User, String) => Route): Route = {
    withBasicAuth { user =>
      withIpAuth(user.allowedIp) { ip =>
        f(user, ip)
      }
    }
  }

  def withBasicAuthIp(f: => Route): Route = {
    withBasicAuth { user =>
      withIpAuth(user.allowedIp) { ip =>
        f
      }
    }
  }

  def withAuth(role: Role)(f: => Route): Route = {
    withBasicAuth { user =>
      withIpAuth(user.allowedIp) { _ =>
        withRoleAuth(role, user) {
          f
        }
      }
    }
  }

  def withAuth(roles: Role*)(f: Set[Role] => Route): Route = {
    withBasicAuth { user =>
      withIpAuth(user.allowedIp) { _ =>
        withRoleAuth(roles.toSet, user) { acceptedRoles =>
          f(acceptedRoles)
        }
      }
    }
  }
}
