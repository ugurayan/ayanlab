package com.ayanlabs.core.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.Instant;
import java.util.UUID;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter
public abstract class BaseEntity {

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
}
