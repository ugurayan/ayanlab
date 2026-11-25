package com.ayanlab.hrm.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Setter
@MappedSuperclass
@SQLRestriction("(\"isDeleted\" = false OR \"isDeleted\" IS NULL)")
public abstract class BaseEntity {

    @Id
    private UUID id;

    @Column(updatable = false, name = "createdAt")
    private Instant createdAt;

    @Column(name = "updatedAt")
    private Instant updatedAt;

    @Column(updatable = false, name = "createdBy")
    private UUID createdBy;

    @Column(name = "updatedBy")
    private UUID updatedBy;

    @Column(name = "isActive")
    private Boolean isActive = true;

    @Column(name = "isDeleted")
    private Boolean isDeleted = false;

    public void markNew(UUID userId) {
        if (id == null) {
            id = UUID.randomUUID();
        }
        Instant now = Instant.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (createdBy == null) {
            createdBy = userId;
        }
        updatedAt = now;
        updatedBy = userId;
        if (isActive == null) {
            isActive = true;
        }
        if (isDeleted == null) {
            isDeleted = false;
        }
    }

    public void markUpdated(UUID userId) {
        updatedAt = Instant.now();
        updatedBy = userId;
    }

    public void markDeleted(UUID userId) {
        isDeleted = true;
        isActive = false;
        markUpdated(userId);
    }

    public boolean live() {
        return !Boolean.TRUE.equals(isDeleted);
    }
}
