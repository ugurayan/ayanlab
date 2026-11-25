package com.ayanlab.hrm.backend.config;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Converts legacy varchar identifier columns to PostgreSQL uuid before Hibernate starts.
 * Slugs such as {@code org_acme} become a stable name-based UUID.
 */
@Slf4j
@Configuration
public class StaffiqUuidColumnMigration {

    private static final Set<String> ID_COLUMNS = Set.of(
            "id", "profileId", "orgId", "workerId", "platformUserId", "platformRecordId", "shiftId", "locationId");
    private static final Set<String> SKIP_TABLES = Set.of("MenuItem", "MenuItemLabel", "Locale", "MockRecord");

    @Bean
    public HibernatePropertiesCustomizer staffiqUuidColumns(DataSource dataSource) {
        migrate(dataSource);
        return properties -> {};
    }

    private void migrate(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            List<String[]> columns = varcharColumns(connection);
            for (String[] column : columns) {
                convert(connection, column[0], column[1]);
            }
            convertTimestamp(connection, "Organization", "createdAt");
            convertTimestamp(connection, "Location", "createdAt");
            dropColumnIfExists(connection, "Shift", "headcount");
            dropColumnIfExists(connection, "Shift", "orgId");
            dropColumnIfExists(connection, "Shift", "role");
            dropColumnIfExists(connection, "Shift", "startAt");
            dropColumnIfExists(connection, "Shift", "endAt");
            dropColumnIfExists(connection, "Location", "orgId");
            dropColumnIfExists(connection, "Organization", "country");
            relaxNonKeyNotNull(connection);
        } catch (Exception ex) {
            log.warn("StaffIQ UUID column migration skipped: {}", ex.getMessage());
        }
    }

    private List<String[]> varcharColumns(Connection connection) throws Exception {
        String sql = """
                SELECT c.relname, a.attname
                FROM pg_class c
                JOIN pg_namespace n ON n.oid = c.relnamespace
                JOIN pg_attribute a ON a.attrelid = c.oid
                JOIN pg_type t ON t.oid = a.atttypid
                WHERE n.nspname = 'staffiq'
                  AND c.relkind = 'r'
                  AND a.attnum > 0
                  AND NOT a.attisdropped
                  AND t.typname IN ('varchar', 'text', 'bpchar')
                """;
        List<String[]> rows = new ArrayList<>();
        try (Statement statement = connection.createStatement(); ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                String table = rs.getString(1);
                String column = rs.getString(2);
                if (!SKIP_TABLES.contains(table) && ID_COLUMNS.contains(column)) {
                    rows.add(new String[] {table, column});
                }
            }
        }
        return rows;
    }

    private void convert(Connection connection, String table, String column) throws Exception {
        String qualified = "staffiq.\"" + table + "\"";
        String tmp = column + "__uuid";
        try (Statement statement = connection.createStatement()) {
            statement.execute("ALTER TABLE " + qualified + " ADD COLUMN IF NOT EXISTS \"" + tmp + "\" uuid");
        }
        Map<String, UUID> mapped = new LinkedHashMap<>();
        try (Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery("SELECT DISTINCT \"" + column + "\" FROM " + qualified + " WHERE \"" + column + "\" IS NOT NULL")) {
            while (rs.next()) {
                String raw = rs.getString(1);
                mapped.put(raw, uuid(raw));
            }
        }
        try (PreparedStatement update = connection.prepareStatement(
                "UPDATE " + qualified + " SET \"" + tmp + "\" = ? WHERE \"" + column + "\" = ? AND \"" + tmp + "\" IS NULL")) {
            for (Map.Entry<String, UUID> entry : mapped.entrySet()) {
                update.setObject(1, entry.getValue());
                update.setString(2, entry.getKey());
                update.addBatch();
            }
            update.executeBatch();
        }
        try (Statement statement = connection.createStatement()) {
            statement.execute("ALTER TABLE " + qualified + " DROP COLUMN \"" + column + "\" CASCADE");
            statement.execute("ALTER TABLE " + qualified + " RENAME COLUMN \"" + tmp + "\" TO \"" + column + "\"");
            if ("id".equals(column)) {
                statement.execute("ALTER TABLE " + qualified + " ADD PRIMARY KEY (\"" + column + "\")");
            }
        }
        log.info("Converted staffiq.{}.{} to uuid", table, column);
    }

    private void convertTimestamp(Connection connection, String table, String column) {
        String sql = """
                SELECT t.typname
                FROM pg_class c
                JOIN pg_namespace n ON n.oid = c.relnamespace
                JOIN pg_attribute a ON a.attrelid = c.oid
                JOIN pg_type t ON t.oid = a.atttypid
                WHERE n.nspname = 'staffiq' AND c.relname = ? AND a.attname = ?
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, table);
            statement.setString(2, column);
            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    return;
                }
                String type = rs.getString(1);
                if (!type.equals("varchar") && !type.equals("text")) {
                    return;
                }
            }
        } catch (Exception ex) {
            log.warn("Could not inspect {}.{}: {}", table, column, ex.getMessage());
            return;
        }
        String alter = "ALTER TABLE staffiq.\"" + table + "\" ALTER COLUMN \"" + column
                + "\" TYPE timestamptz USING CASE WHEN \"" + column + "\" IS NULL OR \"" + column
                + "\" = '' THEN NULL ELSE \"" + column + "\"::timestamptz END";
        try (Statement statement = connection.createStatement()) {
            statement.execute(alter);
        } catch (Exception ex) {
            log.warn("Could not convert {}.{} to timestamptz: {}", table, column, ex.getMessage());
        }
    }

    private void dropColumnIfExists(Connection connection, String table, String column) {
        String exists = """
                SELECT 1 FROM information_schema.columns
                WHERE table_schema = 'staffiq' AND table_name = ? AND column_name = ?
                """;
        try (PreparedStatement statement = connection.prepareStatement(exists)) {
            statement.setString(1, table);
            statement.setString(2, column);
            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    return;
                }
            }
        } catch (Exception ex) {
            log.warn("Could not inspect {}.{}: {}", table, column, ex.getMessage());
            return;
        }
        try (Statement statement = connection.createStatement()) {
            statement.execute("ALTER TABLE staffiq.\"" + table + "\" DROP COLUMN \"" + column + "\" CASCADE");
            log.info("Dropped leftover column staffiq.{}.{}", table, column);
        } catch (Exception ex) {
            log.warn("Could not drop {}.{}: {}", table, column, ex.getMessage());
        }
    }

    private void relaxNonKeyNotNull(Connection connection) {
        String sql = """
                SELECT c.relname, a.attname
                FROM pg_attribute a
                JOIN pg_class c ON c.oid = a.attrelid
                JOIN pg_namespace n ON n.oid = c.relnamespace
                WHERE n.nspname = 'staffiq'
                  AND c.relkind = 'r'
                  AND a.attnum > 0
                  AND NOT a.attisdropped
                  AND a.attnotnull
                  AND NOT EXISTS (
                    SELECT 1 FROM pg_constraint k
                    WHERE k.conrelid = c.oid
                      AND k.contype = 'p'
                      AND a.attnum = ANY (k.conkey)
                  )
                """;
        List<String[]> columns = new ArrayList<>();
        try (Statement statement = connection.createStatement(); ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                columns.add(new String[] {rs.getString(1), rs.getString(2)});
            }
        } catch (Exception ex) {
            log.warn("Could not list staffiq NOT NULL columns: {}", ex.getMessage());
            return;
        }
        for (String[] column : columns) {
            try (Statement statement = connection.createStatement()) {
                statement.execute("ALTER TABLE staffiq.\"" + column[0] + "\" ALTER COLUMN \"" + column[1] + "\" DROP NOT NULL");
            } catch (Exception ex) {
                log.warn("Could not relax {}.{}: {}", column[0], column[1], ex.getMessage());
            }
        }
        if (!columns.isEmpty()) {
            log.info("Relaxed NOT NULL on {} leftover staffiq columns", columns.size());
        }
    }

    private static UUID uuid(String raw) {
        try {
            return UUID.fromString(raw.trim());
        } catch (IllegalArgumentException ex) {
            return UUID.nameUUIDFromBytes(raw.trim().getBytes(StandardCharsets.UTF_8));
        }
    }
}
