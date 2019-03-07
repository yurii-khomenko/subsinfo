DROP KEYSPACE IF EXISTS subsinfo_subsinfo_dict;

CREATE KEYSPACE subsinfo_subsinfo_dict
WITH REPLICATION = { 'class': 'SimpleStrategy', 'replication_factor' : 2 };