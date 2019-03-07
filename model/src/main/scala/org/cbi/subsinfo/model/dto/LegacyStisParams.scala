package org.cbi.subsinfo.model.dto

import java.util.UUID

case class LegacyStisParams(
                             login: String,
                             password: String,
                             channel: String,
                             subs: Long,
                             info: Byte) {

  val cid = UUID.randomUUID
}
