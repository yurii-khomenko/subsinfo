package org.cbi.subsinfo.model.dao

import java.util

import com.datastax.driver.mapping.Result
import com.datastax.driver.mapping.annotations.{Accessor, PartitionKey, Query, Table}

@Accessor trait BlackListNumbersAccessor {
  @Query("SELECT * FROM subsinfo_subsinfo_dict.black_list_numbers WHERE msisdn = :msisdn") def get(msisdn: Long): Result[BlackListNumber]
}

@Table(keyspace = "subsinfo_subsinfo_dict", name = "black_list_numbers", readConsistency = "ONE", writeConsistency = "ONE")
case class BlackListNumber(@PartitionKey msisdn: Long, shortNumbers: util.Set[String]) {
  def this() = this(null.asInstanceOf[Long], null)
}