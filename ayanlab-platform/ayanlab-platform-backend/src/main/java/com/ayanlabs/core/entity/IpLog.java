package com.ayanlabs.core.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/** IP / access log for rate limiting and audit (tmp/auth IpLog). */
@Entity
@Table(name = "IpLog", schema = "core", indexes = {
    @Index(name = "idx_ip_log_address", columnList = "ipAddress"),
    @Index(name = "idx_ip_log_at", columnList = "loggedAt")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IpLog extends BaseEntity {

    @Column(nullable = false, name = "ipAddress", length = 64)
    private String ipAddress;

    @Column(name = "userId")
    private UUID userId;

    @Column(name = "appId")
    private UUID appId;

    @Column(name = "action", length = 128)
    private String action;

    @Column(name = "success")
    private Boolean success;

    @Column(name = "loggedAt")
    private Instant loggedAt;
}