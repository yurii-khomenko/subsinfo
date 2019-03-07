package org.cbi.subsinfo.api.route.resp

import java.util.UUID

import org.cbi.subsinfo.model.dao.Subscriber

import scala.beans.BeanProperty

case class ResponseDto(@BeanProperty cid: UUID, @BeanProperty resource: Subscriber)