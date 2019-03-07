package org.cbi.subsinfo.model.dao

import java.util.Date

import com.datastax.driver.core.Statement
import com.datastax.driver.mapping.Result
import com.datastax.driver.mapping.annotations.{Accessor, PartitionKey, Query, Table}

import scala.beans.BeanProperty

@Accessor trait SubscriberAccessor {
  @Query("SELECT * FROM subsinfo_subsinfo_dict.subscribers WHERE msisdn = :msisdn") def get(msisdn: Long): Result[Subscriber]
  @Query("SELECT * FROM subsinfo_subsinfo_dict.subscribers WHERE msisdn = :msisdn") def getSt(msisdn: Long): Statement
}

@Table(keyspace = "subsinfo_subsinfo_dict", name = "subscribers", readConsistency = "ONE", writeConsistency = "ONE")
case class Subscriber(@PartitionKey msisdn: Long,

                      @BeanProperty subscriberType: Byte,
                      @BeanProperty segmentType: Byte,
                      @BeanProperty languageType: Byte,
                      @BeanProperty hlrType: Byte,
                      @BeanProperty operatorType: Byte,
                      changeDate: Date) {

  def this() = this(
    null.asInstanceOf[Long],
    null.asInstanceOf[Byte],
    null.asInstanceOf[Byte],
    null.asInstanceOf[Byte],
    null.asInstanceOf[Byte],
    null.asInstanceOf[Byte],
    null.asInstanceOf[Date])
}