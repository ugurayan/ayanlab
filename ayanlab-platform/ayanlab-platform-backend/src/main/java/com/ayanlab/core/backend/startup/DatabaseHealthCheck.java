package com.ayanlab.core.backend.startup;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DatabaseHealthCheck {

    private static final Logger log = LoggerFactory.getLogger(DatabaseHealthCheck.class);

    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

  /** From POSTGIS_SCHEMAS (default core) — not every product schema on remote single-DB. */
    private final List<String> requiredSchemas;

    public DatabaseHealthCheck(
            DataSource dataSource,
            @Value("${postgis.schemas:core}") String schemaList) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.requiredSchemas = Arrays.stream(schemaList.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    @PostConstruct
    public void checkAndCreateSchemas() {
        log.info("Running database health check and ensuring required schemas exist");
        try (Connection conn = dataSource.getConnection()) {
            log.info("Connected to database: {}", conn.getMetaData().getURL());
            for (String schema : requiredSchemas) {
                verifyOrCreateSchema(schema);
            }
            log.info("Database schema check completed");
        } catch (SQLException e) {
            log.error("Unable to obtain DB connection during health check", e);
            throw new IllegalStateException("Database is not available", e);
        }
    }

    private void verifyOrCreateSchema(String schema) {
        String safe = schema.replace("\"", "\"\"");
        Integer found = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM information_schema.schemata WHERE schema_name = ?",
                Integer.class,
                schema);
        if (found != null && found > 0) {
            log.info("Schema exists: {}", schema);
            return;
        }
        try {
            jdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS \"" + safe + "\"");
            log.info("Created schema: {}", schema);
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Schema '" + schema + "' is missing and the DB user cannot create it. "
                            + "On remote VPS run: ./scripts/01.remote_db_initialization.sh",
                    e);
        }
    }
}
