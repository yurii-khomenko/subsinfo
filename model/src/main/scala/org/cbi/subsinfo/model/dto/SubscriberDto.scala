package org.cbi.subsinfo.model.dto

import scala.beans.BeanProperty

case class SubscriberDto(
                          @BeanProperty subscriberType: Byte,
                          @BeanProperty segmentType: Byte,
                          @BeanProperty languageType: Byte,
                          @BeanProperty hlrType: Byte,
                          @BeanProperty operatorType: Byte)