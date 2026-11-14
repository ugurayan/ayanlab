package com.ayanlabs.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "EmailDispatchLog", schema = "core")
@Getter
@Setter
@NoArgsConstructor
public class EmailDispatchLog extends BaseEntity {

    @Column(nullable = false, name = "appId")
    private UUID appId;

    @Column(nullable = false, name = "toEmail")
    private String toEmail;

    @Column(name = "templateId")
    private UUID templateId;

    @Column(name = "templateCode")
    private String templateCode;

    @Column(name = "requestPayloadJson", columnDefinition = "text")
    private String requestPayloadJson;

    @Column(name = "renderedSubject")
    private String renderedSubject;

    @Column(name = "status")
    private String status;

    @Column(name = "errorMessage", columnDefinition = "text")
    private String errorMessage;

    @Column(name = "sentAt")
    private Instant sentAt;
}

