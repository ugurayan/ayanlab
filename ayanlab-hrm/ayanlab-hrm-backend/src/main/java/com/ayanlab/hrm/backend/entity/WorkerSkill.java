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

@Getter
@Setter
@Entity
@Table(name = "WorkerSkill")
public class WorkerSkill implements ProfileLinkedEntity {
    @Id
    private String id;

    private String profileId;
    private String orgId;
    private String workerId;
    private String platformUserId;
    private String platformRecordId;
    private String createdAt;
    private String updatedAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private JsonNode details;
}
