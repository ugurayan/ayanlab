package com.ayanlabs.platform.startup;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class DbInitializer {

    private static final String INIT_LOG = "platform/var/init.log";

    private DbInitializer() {}

    private static void appendLog(String s) {
        System.out.println(s);
        try {
            Path p = Path.of("platform/var");
            if (!Files.exists(p)) Files.createDirectories(p);
            try (FileWriter fw = new FileWriter(INIT_LOG, true); PrintWriter pw = new PrintWriter(fw)) {
                pw.println(s);
            }
        } catch (IOException ignored) {
            // best-effort logging; don't fail initialization because of logging
        }
    }

    /**
     * Ensure the named database exists on the Postgres server. Connects to the 'postgres' database
     * as the specified user and password and runs CREATE DATABASE if the target database is missing.
     *
     * @param host Postgres host
     * @param port Postgres port
     * @param dbName target database name to ensure
     * @param dbUser user to connect as (must have rights to create databases)
     * @param dbPassword password for dbUser
     * @throws SQLException on JDBC errors
     */
    public static boolean canConnectToDatabase(String host, int port, String dbName, String dbUser, String dbPassword) {
        String targetUrl = String.format("jdbc:postgresql://%s:%d/%s", host, port, dbName);
        try (Connection ignored = DriverManager.getConnection(targetUrl, dbUser, dbPassword)) {
            return true;
        } catch (SQLException e) {
            appendLog("Cannot connect to target DB " + targetUrl + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * If {@code dbName} differs only by case from an existing database (e.g. ayanlabs vs ayanLabs),
     * returns the actual {@code datname} from pg_database.
     */
    public static Optional<String> findDatabaseNameCaseMismatch(
        String host, int port, String dbName, String dbUser, String dbPassword
    ) throws SQLException {
        String maintenanceUrl = String.format("jdbc:postgresql://%s:%d/postgres", host, port);
        String sql = "SELECT datname FROM pg_database WHERE lower(datname) = lower('"
            + escapeLiteral(dbName) + "') AND datistemplate = false";
        try (Connection conn = DriverManager.getConnection(maintenanceUrl, dbUser, dbPassword);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            List<String> names = new ArrayList<>();
            while (rs.next()) {
                names.add(rs.getString(1));
            }
            if (names.size() == 1 && !names.get(0).equals(dbName)) {
                return Optional.of(names.get(0));
            }
        }
        return Optional.empty();
    }

    /** Returns true if the database row exists in pg_database (connects to maintenance DB `postgres`). */
    public static boolean databaseExists(String host, int port, String dbName, String dbUser, String dbPassword) throws SQLException {
        String maintenanceUrl = String.format("jdbc:postgresql://%s:%d/postgres", host, port);
        try (Connection conn = DriverManager.getConnection(maintenanceUrl, dbUser, dbPassword);
             Statement stmt = conn.createStatement()) {
            String checkSql = "SELECT 1 FROM pg_database WHERE datname = '" + escapeLiteral(dbName) + "'";
            try (ResultSet rs = stmt.executeQuery(checkSql)) {
                return rs.next();
            }
        }
    }

    public static void ensureDatabaseExists(String host, int port, String dbName, String dbUser, String dbPassword) throws SQLException {
        if (canConnectToDatabase(host, port, dbName, dbUser, dbPassword)) {
            appendLog("Database '" + dbName + "' is reachable");
            return;
        }

        String maintenanceUrl = String.format("jdbc:postgresql://%s:%d/postgres", host, port);
        appendLog("Connecting to maintenance DB: " + maintenanceUrl + " as user '" + dbUser + "'");
        try (Connection conn = DriverManager.getConnection(maintenanceUrl, dbUser, dbPassword);
             Statement stmt = conn.createStatement()) {

            String checkSql = "SELECT 1 FROM pg_database WHERE datname = '" + escapeLiteral(dbName) + "'";
            try (ResultSet rs = stmt.executeQuery(checkSql)) {
                if (rs.next()) {
                    appendLog("Database '" + dbName + "' exists but is not reachable with current credentials");
                    return;
                }
            }

            String createSql = "CREATE DATABASE \"" + dbName.replace("\"", "\"\"") + "\"";
            stmt.executeUpdate(createSql);
            appendLog("Created database '" + dbName + "'");
        }
    }

    public static void printDatabaseMissingHelp(
        String host, int port, String dbName, String dbUser, Optional<String> caseMismatchDb
    ) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(
            "%nDatabase '%s' does not exist on %s:%d and user '%s' cannot create it.%n",
            dbName, host, port, dbUser
        ));
        caseMismatchDb.ifPresent(actual -> sb.append(String.format(
            "Found a database with the same name but different casing: '%s'.%n"
                + "Set POSTGIS_DB=%s in the platform .env (names are case-sensitive in PostgreSQL).%n",
            actual, actual
        )));
        sb.append(String.format(
            "Create it once as a Postgres superuser (on the VPS):%n"
                + "  docker exec -it postgis psql -U postgres -d postgres -c \"CREATE DATABASE \\\"%s\\\" OWNER %s;\"%n"
                + "Connect with psql: docker exec -it postgis psql -U %s -d postgres%n"
                + "Or set POSTGIS_DB to an existing database name from your PostGIS compose.%n",
            dbName, dbUser, dbUser
        ));
        String msg = sb.toString();
        System.err.println(msg);
        appendLog(msg.trim());
    }

    /**
     * Ensure the provided schemas exist inside the target database. Connects to the target DB and
     * runs CREATE SCHEMA IF NOT EXISTS for each schema. This must be run before Hibernate initializes.
     */
    public static void ensureSchemasExist(String host, int port, String dbName, String dbUser, String dbPassword, List<String> schemas) throws SQLException {
        String targetUrl = String.format("jdbc:postgresql://%s:%d/%s", host, port, dbName);
        appendLog("Connecting to target DB: " + targetUrl + " as user '" + dbUser + "' to ensure schemas");
        try (Connection conn = DriverManager.getConnection(targetUrl, dbUser, dbPassword);
             Statement stmt = conn.createStatement()) {
            for (String schema : schemas) {
                String sql = "CREATE SCHEMA IF NOT EXISTS \"" + schema.replace("\"", "\"\"") + "\"";
                stmt.execute(sql);
                appendLog("Ensured schema exists: " + schema);
            }
            applyCoreSchemaPatches(stmt);
        }
    }

    /**
     * Idempotent DDL patches that Hibernate ddl-auto cannot apply safely on existing data
     * (e.g. adding NOT NULL columns to tables that already have rows).
     */
    private static void applyCoreSchemaPatches(Statement stmt) throws SQLException {
        patchProfilePermissionEffect(stmt);
        ensureApplicationUserTable(stmt);
    }

    /**
     * Hibernate ddl-auto=update is not creating this new join table on existing local DBs.
     */
    private static void ensureApplicationUserTable(Statement stmt) throws SQLException {
        stmt.execute(
            "CREATE TABLE IF NOT EXISTS \"core\".\"ApplicationUser\" ("
                + "\"id\" uuid NOT NULL, "
                + "\"createdAt\" timestamp with time zone, "
                + "\"createdBy\" uuid, "
                + "\"isActive\" boolean, "
                + "\"updatedAt\" timestamp with time zone, "
                + "\"updatedBy\" uuid, "
                + "\"appId\" uuid NOT NULL, "
                + "\"userId\" uuid NOT NULL, "
                + "PRIMARY KEY (\"id\"), "
                + "CONSTRAINT \"UK_ApplicationUser_appId_userId\" UNIQUE (\"appId\", \"userId\")"
                + ")"
        );
        appendLog("Ensured table core.ApplicationUser");
    }

    private static void patchProfilePermissionEffect(Statement stmt) throws SQLException {
        try (ResultSet tableRs = stmt.executeQuery(
            "SELECT 1 FROM information_schema.tables "
                + "WHERE table_schema = 'core' AND table_name = 'ProfilePermission'"
        )) {
            if (!tableRs.next()) {
                return;
            }
        }

        String table = "\"core\".\"ProfilePermission\"";
        try (ResultSet rs = stmt.executeQuery(
            "SELECT 1 FROM information_schema.columns "
                + "WHERE table_schema = 'core' AND table_name = 'ProfilePermission' AND column_name = 'effect'"
        )) {
            if (!rs.next()) {
                stmt.execute("ALTER TABLE " + table + " ADD COLUMN \"effect\" varchar(255)");
                appendLog("Added column core.ProfilePermission.effect");
            }
        }
        stmt.executeUpdate("UPDATE " + table + " SET \"effect\" = 'GRANT' WHERE \"effect\" IS NULL");
        stmt.execute("ALTER TABLE " + table + " ALTER COLUMN \"effect\" SET NOT NULL");
        appendLog("Patched core.ProfilePermission.effect (backfill GRANT, NOT NULL)");
    }

    private static String escapeLiteral(String s) {
        return s.replace("'", "''");
    }
}
