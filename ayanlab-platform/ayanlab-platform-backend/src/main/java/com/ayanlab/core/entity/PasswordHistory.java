package com.ayanlab.core.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/** Prior password hashes (tmp/auth PasswordHistory). */
@Entity
@Table(name = "PasswordHistory", schema = "core", indexes = @Index(name = "idx_password_history_user", columnList = "userId"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordHistory extends BaseEntity {

    @Column(nullable = false, name = "userId")
    private UUID userId;

    @Column(nullable = false, name = "passwordHash", length = 512)
    private String passwordHash;

    @Column(name = "changedAt")
    private Instant changedAt;
}