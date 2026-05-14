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
import java.util.List;

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
    public static void ensureDatabaseExists(String host, int port, String dbName, String dbUser, String dbPassword) throws SQLException {
        String maintenanceUrl = String.format("jdbc:postgresql://%s:%d/postgres", host, port);
        appendLog("Connecting to maintenance DB: " + maintenanceUrl + " as user '" + dbUser + "'");
        try (Connection conn = DriverManager.getConnection(maintenanceUrl, dbUser, dbPassword);
             Statement stmt = conn.createStatement()) {

            String checkSql = "SELECT 1 FROM pg_database WHERE datname = '" + escapeLiteral(dbName) + "'";
            try (ResultSet rs = stmt.executeQuery(checkSql)) {
                if (rs.next()) {
                    appendLog("Database '" + dbName + "' already exists");
                    return;
                }
            }

            String createSql = "CREATE DATABASE \"" + dbName.replace("\"", "\"\"") + "\"";
            stmt.executeUpdate(createSql);
            appendLog("Created database '" + dbName + "'");
        }
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
        }
    }

    private static String escapeLiteral(String s) {
        return s.replace("'", "''");
    }
}
