package com.ayanlabs.core.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/** i18n key/value per app (tmp/auth Translation). */
@Entity
@Table(name = "Translation", schema = "core",
       uniqueConstraints = @UniqueConstraint(columnNames = {"appId", "language", "messageKey"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Translation extends BaseEntity {

    @Column(nullable = false, name = "appId")
    private UUID appId;

    @Column(nullable = false, name = "language", length = 16)
    private String language;

    @Column(nullable = false, name = "messageKey", length = 256)
    private String messageKey;

    @Column(nullable = false, name = "messageValue", columnDefinition = "text")
    private String messageValue;
}