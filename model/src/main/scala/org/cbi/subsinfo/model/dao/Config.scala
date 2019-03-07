package org.cbi.subsinfo.model.dao

import com.datastax.driver.mapping.Result
import com.datastax.driver.mapping.annotations.{Accessor, PartitionKey, Query, Table}

@Accessor trait ConfigAccessor {
  @Query("SELECT * FROM subsinfo_subsinfo_dict.configs") def getAll: Result[Config]
}

@Table(keyspace = "subsinfo_subsinfo_dict", name = "configs", readConsistency = "ONE", writeConsistency = "ONE")
case class Config(@PartitionKey key: String, value: String) {
  def this() = this(null, null)
}