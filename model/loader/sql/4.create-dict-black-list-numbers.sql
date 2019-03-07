USE subsinfo_subsinfo_dict;

CREATE TABLE black_list_numbers (
  msisdn BIGINT PRIMARY KEY,
  shortNumbers SET<TEXT>
);