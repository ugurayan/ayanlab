package com.ayanlabs.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@SpringBootApplication
@ComponentScan(basePackages = "com.ayanlabs")
@EntityScan(basePackages = "com.ayanlabs")
@EnableJpaRepositories(basePackages = "com.ayanlabs")
public class PlatformApplication {
    public static void main(String[] args) {
        // Read DB initializer settings from environment with defaults
        String host = System.getenv().getOrDefault("POSTGIS_HOST", "147.93.138.158");
        int port = Integer.parseInt(System.getenv().getOrDefault("POSTGIS_PORT", "5432"));
        String dbName = System.getenv().getOrDefault("POSTGIS_DB", "ayanLabs");
        String dbUser = System.getenv().getOrDefault("POSTGIS_USER", "core");
        String dbPassword = System.getenv().getOrDefault("POSTGIS_PASSWORD", "UgurAyanLabs9602.");
        String schema = System.getenv().getOrDefault("POSTGIS_SCHEMA", "core");

        // Determine schemas list: POSTGIS_SCHEMAS env var (comma separated) or ayanlabs.db.schemas in application.yml
        String schemasEnv = System.getenv("POSTGIS_SCHEMAS");
        List<String> schemas;
        if (schemasEnv != null && !schemasEnv.isBlank()) {
            schemas = Arrays.stream(schemasEnv.split(",")).map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());
        } else {
            schemas = readSchemasFromApplicationYaml().orElse(List.of(schema, "ipms", "crm", "vault","staffiq"));
        }

        boolean dbReady = false;
        try {
            com.ayanlabs.platform.startup.DbInitializer.ensureDatabaseExists(host, port, dbName, dbUser, dbPassword);
            dbReady = true;
        } catch (Exception e) {
            // couldn't ensure DB exists (likely permission or auth). Try to connect directly to target DB.
            System.err.println("Warning: ensureDatabaseExists failed: " + e.getMessage());
            appendInitLog("Warning: ensureDatabaseExists failed: " + e.getMessage());
            try {
                String targetUrl = String.format("jdbc:postgresql://%s:%d/%s", host, port, dbName);
                appendInitLog("Attempting direct connection to target DB: " + targetUrl + " as user '" + dbUser + "'");
                try (Connection c = DriverManager.getConnection(targetUrl, dbUser, dbPassword)) {
                    appendInitLog("Direct connection to target DB succeeded; assuming database exists.");
                    dbReady = true;
                }
            } catch (Exception ex) {
                System.err.println("ERROR: could not connect to target DB after failed ensureDatabaseExists: " + ex.getMessage());
                appendInitLog("ERROR: could not connect to target DB after failed ensureDatabaseExists: " + ex.getMessage());
                // abort now — can't proceed
                System.err.println("Aborting startup due to DB connectivity issues.");
                appendInitLog("Aborting startup due to DB connectivity issues.");
                System.exit(1);
            }
        }

        if (dbReady) {
            try {
                com.ayanlabs.platform.startup.DbInitializer.ensureSchemasExist(host, port, dbName, dbUser, dbPassword, schemas);
            } catch (Exception e) {
                // log and continue — schemas may already exist or permissions may be limited
                System.err.println("Warning: could not ensure schemas: " + e.getMessage());
                appendInitLog("Warning: could not ensure schemas: " + e.getMessage());
            }
        }

        SpringApplication.run(PlatformApplication.class, args);
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
            int idx = content.indexOf("db.schemas:");
            if (idx >= 0) {
                int lineEnd = content.indexOf('\n', idx);
                if (lineEnd < 0) lineEnd = content.length();
                String line = content.substring(idx, lineEnd);
                String value = line.substring(line.indexOf(":") + 1).trim();
                if (value.startsWith("\"") && value.endsWith("\"")) {
                    value = value.substring(1, value.length()-1);
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
