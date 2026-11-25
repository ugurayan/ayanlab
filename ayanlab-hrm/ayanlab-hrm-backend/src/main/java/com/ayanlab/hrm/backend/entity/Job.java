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
@Table(name = "Job")
public class Job implements ProfileLinkedEntity {
    @Id
    private UUID id;

    private UUID profileId;
    private UUID orgId;
    private UUID workerId;
    private UUID platformUserId;
    private UUID platformRecordId;
    private String createdAt;
    private String updatedAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private JsonNode details;
}
