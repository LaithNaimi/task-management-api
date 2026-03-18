SELECT setval(
               pg_get_serial_sequence('app_user', 'id'),
               (SELECT COALESCE(MAX(id), 0) FROM app_user)
       );
