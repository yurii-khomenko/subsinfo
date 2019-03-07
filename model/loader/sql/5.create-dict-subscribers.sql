USE subsinfo_subsinfo_dict;

CREATE TABLE subscribers (

  msisdn BIGINT PRIMARY KEY,

  subscriberType TINYINT,
  segmentType TINYINT,
  languageType TINYINT,
  hlrType TINYINT,
  operatorType TINYINT,

  changeDate TIMESTAMP
);