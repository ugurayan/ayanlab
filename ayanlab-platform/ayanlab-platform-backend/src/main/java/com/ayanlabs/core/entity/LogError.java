package com.ayanlabs.core.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/** Async error/audit log (tmp/auth LogError). */
@Entity
@Table(name = "LogError", schema = "core", indexes = {
    @Index(name = "idx_log_error_status", columnList = "status"),
    @Index(name = "idx_log_error_user", columnList = "userId")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogError extends BaseEntity {

    @Column(name = "status", length = 32)
    private String status;

    @Column(name = "userId")
    private UUID userId;

    @Column(name = "appId")
    private UUID appId;

    @Column(name = "requestPath", length = 512)
    private String requestPath;

    @Column(name = "httpMethod", length = 16)
    private String httpMethod;

    @Column(name = "errorCode", length = 64)
    private String errorCode;

    @Column(name = "message", columnDefinition = "text")
    private String message;

    @Column(name = "stackTrace", columnDefinition = "text")
    private String stackTrace;

    @Column(name = "ipAddress", length = 64)
    private String ipAddress;
}