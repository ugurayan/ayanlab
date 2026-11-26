package com.ayanlab.comm.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class BaseEntity implements Persistable<UUID> {

    @Id
    @Column(name = "id")
    private UUID id;

    @CreatedDate
    @JsonProperty("createdAt")
    @Column(updatable = false, name = "createdat")
    private Instant createdAt;

    @LastModifiedDate
    @JsonProperty("updatedAt")
    @Column(updatable = true, name = "updatedat")
    private Instant updatedAt;

    @CreatedBy
    @JsonProperty("createdBy")
    @Column(updatable = false, name = "createdby")
    private UUID createdBy;

    @LastModifiedBy
    @JsonProperty("updatedBy")
    @Column(updatable = true, name = "updatedby")
    private UUID updatedBy;

    @JsonProperty("isActive")
    @Column(name = "isactive")
    private Boolean isActive = true;

    @Transient
    @JsonIgnore
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private boolean newEntity = true;

    @Override
    @JsonIgnore
    public boolean isNew() {
        return newEntity;
    }

    @PrePersist
    @PostLoad
    void markNotNew() {
        this.newEntity = false;
    }

    public void markNew() {
        this.newEntity = true;
    }
}
