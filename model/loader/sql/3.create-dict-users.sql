USE subsinfo_subsinfo_dict;

CREATE TABLE users (

  id UUID PRIMARY KEY,

  login TEXT,
  password TEXT,
  allowedIp SET<TEXT>,
  roles SET<TEXT>
);