package com.ayanlab.hrm.backend.entity;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * JSON payload rows for mock collections (jobs, transactions, job catalog, departments, …).
 */
@Getter
@Setter
@Entity
@Table(name = "MockRecord")
public class MockRecord {
    @Id
    private String id;

    @Column(nullable = false)
    private String collection;

    @Column(nullable = false)
    private String recordKey;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private JsonNode payload;
}