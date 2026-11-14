DB initialization & default data

This project now uses Hibernate/JPA schema generation to create and update database schemas and tables automatically.

Key points:
- Hibernate auto DDL is enabled: `spring.jpa.hibernate.ddl-auto=update` in `platform/src/main/resources/application.yml`.
- The SQL scripts `db/init_full_schema.sql` and `db/rename_tables_to_titlecase.sql` have been intentionally disabled (left as placeholders). Do not rely on them; Hibernate will manage schema creation.
- Default data (applications, roles, permissions, users, profiles) is loaded from JSON by `DefaultDataLoader` when enabled.

Default data loader
- File: `platform/src/main/java/com/ayanlabs/platform/startup/DefaultDataLoader.java`
- JSON: `platform/src/main/resources/db/default-data.json`
- Enable via environment or application.yml:
  - `platform.db.load-default-data=true`

Security and notes
- Creating PostGIS/pgcrypto extensions still may require superuser access; if your environment doesn't have extensions installed, ask DBA to create them. Hibernate can't create PostGIS extension for you unless the DB user has superuser privileges.
- Default-data JSON contains plaintext passwords for initial bootstrap (use only in dev or CI). For production, use a secure secrets store.

To run
1) Ensure DB connectivity settings (POSTGIS_HOST, POSTGIS_DB, POSTGIS_USER, POSTGIS_PASSWORD) are set.
2) Enable data load if needed:

```bash
export PLATFORM_DB_LOAD_DEFAULT_DATA=true
./gradlew :platform:bootRun
```

If you want to switch back to manual SQL scripts, restore/replace `db/init_full_schema.sql` and `db/rename_tables_to_titlecase.sql` and set `platform.db.init-on-startup=true` (the runner will log a message when scripts are disabled).
