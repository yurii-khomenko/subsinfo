package org.cbi.subsinfo.api.auth

import java.net.InetAddress
import java.util

import akka.http.scaladsl.model.RemoteAddress.IP
import akka.http.scaladsl.model.StatusCodes.Forbidden
import akka.http.scaladsl.server.{Directives, Route}

import scala.collection.JavaConverters._

trait IpAuth extends Directives {

  def withIpAuth(allowedIps: util.Set[String])(f: String => Route): Route = {
    extractClientIP { remote =>
      val ip = remote.toIP.getOrElse(IP(InetAddress.getByName("127.0.0.1"))).ip.getHostAddress
      if (matchIp(ip, allowedIps)) f(ip)
      else complete((Forbidden, s"ip not allowed: $ip"))
    }
  }

  def matchIp(ip: String, patternIps: util.Set[String]): Boolean = {
    patternIps.contains(ip) || patternIps.asScala.toStream.exists((patternIp: String) => matchIp(ip, patternIp))
  }

  def matchIp(ip: String, patternIp: String): Boolean  = {
    if (ip == null || patternIp == null) throw new IllegalArgumentException("matchIp incorrect arguments: ip:" + ip + " patternIp:" + patternIp)
    if (ip.contains(".")) return matchIpV4(ip, patternIp)
    throw new IllegalArgumentException("ip incorrect:" + ip)
  }

  private def matchIpV4(ip: String, patternIp: String): Boolean  = {

    if (!patternIp.contains(".")) return false

    val userIp = ip.split("\\.")
    if (userIp.length != 4) throw new IllegalArgumentException("ip v4 incorrect:" + patternIp)

    val pattern = patternIp.split("\\.")
    if (pattern.length != 4) throw new IllegalArgumentException("patternIp v4 incorrect:" + patternIp)
    (pattern(0) == "*" || pattern(0) == userIp(0)) &&
      (pattern(1) == "*" || pattern(1) == userIp(1)) &&
      (pattern(2) == "*" || pattern(2) == userIp(2)) &&
      (pattern(3) == "*" || pattern(3) == userIp(3))
  }
}
