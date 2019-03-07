USE subsinfo_subsinfo_dict;

INSERT INTO users JSON '{
  "id": "72a599ac-12b7-469a-86d9-c078b859c2dc",
  "login": "test-client",
  "password": "test-client-password",
  "allowedIp": ["127.0.0.1", "10.44.*.*", "10.49.*.*", "192.168.*.*"],
  "roles": ["client"]
}';

INSERT INTO users JSON '{
  "id": "9af0419d-d103-4d2e-84fd-2290a4833105",
  "login": "test-updater",
  "password": "test-updater-password",
  "allowedIp": ["127.0.0.1", "10.44.*.*", "10.49.*.*", "192.168.*.*"],
  "roles": ["updater"]
}';