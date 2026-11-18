package com.ayanlab.core.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/** Login attempts / sessions audit (tmp/auth LoginHistory). */
@Entity
@Table(name = "LoginHistory", schema = "core", indexes = {
    @Index(name = "idx_login_history_user", columnList = "userId"),
    @Index(name = "idx_login_history_at", columnList = "loginAt")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginHistory extends BaseEntity {

    @Column(nullable = false, name = "userId")
    private UUID userId;

    @Column(name = "appId")
    private UUID appId;

    @Column(name = "profileId")
    private UUID profileId;

    @Column(name = "ipAddress", length = 64)
    private String ipAddress;

    @Column(name = "userAgent", length = 512)
    private String userAgent;

    @Column(name = "success")
    private Boolean success;

    @Column(name = "failureReason", length = 256)
    private String failureReason;

    @Column(name = "loginAt")
    private Instant loginAt;
}