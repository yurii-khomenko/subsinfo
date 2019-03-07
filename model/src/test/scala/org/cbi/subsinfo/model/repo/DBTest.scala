package org.cbi.subsinfo.model.repo

import org.cbi.subsinfo.model.Config
import org.cbi.subsinfo.model.dao.BlackListNumber
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

import scala.collection.JavaConverters._

@RunWith(classOf[JUnitRunner])
class DBTest extends Config with DB {

  s"The DB subsystem" should {

    s"1. Get BlackListRule" in {

      val expected = BlackListNumber(380670000001L, Set("777", "bigtits", "pravexbank").asJava)
      val actual = blackListRuleAccessor.get(380670000001L).one

      actual shouldEqual expected
    }
  }
}