package com.ayanlabs.core.entity;

import com.ayanlabs.core.data.enums.NotificationTypeEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "NotificationTemplate", schema = "core",
       uniqueConstraints = {@UniqueConstraint(columnNames = {"appId","code","type","language"})})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationTemplate extends BaseEntity {

    @Column(nullable = false)
    private java.util.UUID appId;

    @Column(nullable = false)
    @NotBlank
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationTypeEnum type;

    @Column(nullable = true)
    private String language;

    // For email templates
    private String subject;

    @Column(columnDefinition = "text")
    private String body;

    private String description;

    @Column(name = "templateVersion")
    private Integer templateVersion;

    @Column(name = "parametersJson", columnDefinition = "text")
    private String parametersJson;

    @Column(name = "isSystem")
    private Boolean isSystem;
}

