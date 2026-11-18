package com.ayanlab.core.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@SpringBootApplication
@ComponentScan(basePackages = "com.ayanlab")
@EntityScan(basePackages = "com.ayanlab")
@EnableJpaRepositories(basePackages = "com.ayanlab")
public class PlatformApplication {
    public static void main(String[] args) {
        String host = System.getenv().getOrDefault("POSTGIS_HOST", "localhost");
        int port = Integer.parseInt(System.getenv().getOrDefault("POSTGIS_PORT", "5432"));
        String dbName = System.getenv().getOrDefault("POSTGIS_DB", "ayanLabs");
        String dbUser = System.getenv().getOrDefault("POSTGIS_USER", "core");
        String dbPassword = System.getenv().getOrDefault("POSTGIS_PASSWORD", "");
        String schema = System.getenv().getOrDefault("POSTGIS_SCHEMA", "core");

        String schemasEnv = System.getenv("POSTGIS_SCHEMAS");
        List<String> schemas;
        if (schemasEnv != null && !schemasEnv.isBlank()) {
            schemas = Arrays.stream(schemasEnv.split(",")).map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());
        } else {
            schemas = readSchemasFromApplicationYaml().orElse(List.of(schema, "ipms", "crm", "vault", "staffiq"));
        }

        if (!ensureDatabaseReady(host, port, dbName, dbUser, dbPassword)) {
            System.exit(1);
        }

        try {
            com.ayanlab.core.backend.startup.DbInitializer.ensureSchemasExist(host, port, dbName, dbUser, dbPassword, schemas);
        } catch (Exception e) {
            System.err.println("Warning: could not ensure schemas: " + e.getMessage());
            appendInitLog("Warning: could not ensure schemas: " + e.getMessage());
        }

        SpringApplication.run(PlatformApplication.class, args);
    }

    private static boolean ensureDatabaseReady(String host, int port, String dbName, String dbUser, String dbPassword) {
        if (com.ayanlab.core.backend.startup.DbInitializer.canConnectToDatabase(host, port, dbName, dbUser, dbPassword)) {
            appendInitLog("Target database is reachable: " + dbName);
            return true;
        }

        Optional<String> caseMismatch = Optional.empty();
        try {
            caseMismatch = com.ayanlab.core.backend.startup.DbInitializer.findDatabaseNameCaseMismatch(
                host, port, dbName, dbUser, dbPassword
            );
            if (caseMismatch.isPresent()) {
                String actual = caseMismatch.get();
                System.err.println("ERROR: database exists as '" + actual + "' but POSTGIS_DB is '" + dbName + "'.");
                System.err.println("  Set POSTGIS_DB=" + actual + " in /root/dockers/platform/.env and redeploy.");
                appendInitLog("Case mismatch: configured=" + dbName + " actual=" + actual);
                com.ayanlab.core.backend.startup.DbInitializer.printDatabaseMissingHelp(
                    host, port, dbName, dbUser, caseMismatch
                );
                return false;
            }
            if (com.ayanlab.core.backend.startup.DbInitializer.databaseExists(host, port, dbName, dbUser, dbPassword)) {
                System.err.println("ERROR: database '" + dbName + "' exists but connection failed (check POSTGIS_USER/POSTGIS_PASSWORD).");
                appendInitLog("Database exists but connection failed.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Warning: could not inspect databases: " + e.getMessage());
            appendInitLog("Warning: could not inspect databases: " + e.getMessage());
        }

        try {
            com.ayanlab.core.backend.startup.DbInitializer.ensureDatabaseExists(host, port, dbName, dbUser, dbPassword);
            if (com.ayanlab.core.backend.startup.DbInitializer.canConnectToDatabase(host, port, dbName, dbUser, dbPassword)) {
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Warning: ensureDatabaseExists failed: " + e.getMessage());
            appendInitLog("Warning: ensureDatabaseExists failed: " + e.getMessage());
        }

        com.ayanlab.core.backend.startup.DbInitializer.printDatabaseMissingHelp(host, port, dbName, dbUser, caseMismatch);
        System.err.println("Aborting startup due to DB connectivity issues.");
        appendInitLog("Aborting startup due to DB connectivity issues.");
        return false;
    }

    private static void appendInitLog(String msg) {
        try {
            Path p = Path.of("platform/var");
            if (!Files.exists(p)) Files.createDirectories(p);
            Files.writeString(Path.of("platform/var/init.log"), msg + "\n", java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
        } catch (IOException ignored) {}
    }

    private static Optional<List<String>> readSchemasFromApplicationYaml() {
        try {
            Path p = Path.of("platform/src/main/resources/application.yml");
            if (!Files.exists(p)) return Optional.empty();
            String content = Files.readString(p);
            int idx = content.indexOf("schemas:");
            if (idx < 0) idx = content.indexOf("db.schemas:");
            if (idx >= 0) {
                int lineEnd = content.indexOf('\n', idx);
                if (lineEnd < 0) lineEnd = content.length();
                String line = content.substring(idx, lineEnd);
                String value = line.substring(line.indexOf(":") + 1).trim();
                if (value.startsWith("\"") && value.endsWith("\"")) {
                    value = value.substring(1, value.length() - 1);
                }
                if (!value.isEmpty()) {
                    return Optional.of(Arrays.stream(value.split(",")).map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList()));
                }
            }
            return Optional.empty();
        } catch (IOException e) {
            return Optional.empty();
        }
    }
}
