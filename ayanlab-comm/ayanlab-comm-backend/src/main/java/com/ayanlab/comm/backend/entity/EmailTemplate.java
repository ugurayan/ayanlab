package com.ayanlab.comm.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "email_templates", schema = "comm")
@Getter
@Setter
@NoArgsConstructor
public class EmailTemplate extends BaseEntity {

    @Column(nullable = false, name = "organization_id")
    private UUID organizationId;

    @Column(nullable = false, name = "app_id")
    private UUID appId;

    @Column(nullable = false, name = "name")
    private String name;

    @Column(name = "default_language", length = 10)
    private String defaultLanguage = "en";

    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<EmailTemplateTranslation> translations = new HashSet<>();
}
