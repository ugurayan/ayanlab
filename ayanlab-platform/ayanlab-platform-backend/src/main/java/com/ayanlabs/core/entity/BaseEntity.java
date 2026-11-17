package com.ayanlabs.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

/**
 * Application-assigned UUID ids (see {@link com.ayanlabs.core.util.IdGenerator}).
 * Implements {@link Persistable} so Spring Data uses {@code persist()} for new rows
 * instead of {@code merge()} when the id is pre-set — merge-with-assigned-id caused
 * {@code Cannot invoke "Object.hashCode()" because "pk" is null} on signup/register.
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class BaseEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @CreatedDate
    @JsonProperty("createdAt")
    @Column(updatable = false, name = "createdAt")
    private Instant createdAt;

    @LastModifiedDate
    @JsonProperty("updatedAt")
    @Column(updatable = true, name = "updatedAt")
    private Instant updatedAt;

    @CreatedBy
    @JsonProperty("createdBy")
    @Column(updatable = false, name = "createdBy")
    private UUID createdBy;

    @LastModifiedBy
    @JsonProperty("updatedBy")
    @Column(updatable = false, name = "updatedBy")
    private UUID updatedBy;

    @JsonProperty("isActive")
    @Column(name = "isActive")
    private Boolean isActive = true;

    /**
     * True until first persist/load. Required because ids are assigned in-app before save.
     */
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

    /** Call when constructing a brand-new aggregate that already has an id. */
    public void markNew() {
        this.newEntity = true;
    }
}
