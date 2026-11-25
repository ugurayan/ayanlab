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
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "TransactionRecord")
public class TransactionRecord implements ProfileLinkedEntity {
    @Id
    private UUID id;

    private UUID profileId;
    private UUID orgId;
    private UUID workerId;
    private UUID platformUserId;
    private UUID platformRecordId;
    private String createdAt;
    private String updatedAt;
    private Long amountCents;
    private String currency;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private JsonNode details;
}
