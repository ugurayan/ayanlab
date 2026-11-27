package com.ayanlab.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/** ISO 639 language reference row (seeded from {@code db/language-catalog.json}). */
@Entity
@Table(
    name = "ReferenceLanguage",
    schema = "core",
    uniqueConstraints = @UniqueConstraint(name = "UK_ReferenceLanguage_code", columnNames = "code"),
    indexes = @Index(name = "idx_reference_language_code", columnList = "code")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReferenceLanguage extends BaseEntity {

    @NotBlank
    @Size(max = 16)
    @Column(nullable = false, name = "code", length = 16)
    private String code;

    @NotBlank
    @Column(nullable = false, name = "name", length = 256)
    private String name;

    @Column(name = "nativeName", length = 256)
    private String nativeName;
}