package com.ayanlabs.platform.startup;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

@Component
public class DatabaseHealthCheck {

    private static final Logger log = LoggerFactory.getLogger(DatabaseHealthCheck.class);

    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    // list of schemas that should exist; add module schemas here
    private final List<String> requiredSchemas = Arrays.asList("core", "ipms", "crm", "vault");

    public DatabaseHealthCheck(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @PostConstruct
    public void checkAndCreateSchemas() {
        log.info("Running database health check and ensuring required schemas exist");
        try (Connection conn = dataSource.getConnection()) {
            log.info("Connected to database: {}", conn.getMetaData().getURL());
            for (String schema : requiredSchemas) {
                createSchemaIfNotExists(schema);
            }
            log.info("Database schema check completed");
        } catch (SQLException e) {
            log.error("Unable to obtain DB connection during health check", e);
            throw new IllegalStateException("Database is not available", e);
        }
    }

    private void createSchemaIfNotExists(String schema) {
        try {
            String sql = String.format("CREATE SCHEMA IF NOT EXISTS %s", schema);
            jdbcTemplate.execute(sql);
            log.info("Ensured schema exists: {}", schema);
        } catch (Exception e) {
            log.error("Failed to create or verify schema {}", schema, e);
            // propagate as runtime to fail fast if needed
            throw e;
        }
    }
}
