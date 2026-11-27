package com.ayanlab.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/** ISO 3166 country reference row (seeded from {@code db/country-catalog.json}). */
@Entity
@Table(
    name = "ReferenceCountry",
    schema = "core",
    uniqueConstraints = @UniqueConstraint(name = "UK_ReferenceCountry_alpha2", columnNames = "alpha2Code"),
    indexes = {
        @Index(name = "idx_reference_country_alpha2", columnList = "alpha2Code"),
        @Index(name = "idx_reference_country_alpha3", columnList = "alpha3Code")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReferenceCountry extends BaseEntity {

    @NotBlank
    @Size(min = 2, max = 2)
    @Column(nullable = false, name = "alpha2Code", length = 2)
    private String alpha2Code;

    @NotBlank
    @Column(nullable = false, name = "name", length = 256)
    private String name;

    @NotBlank
    @Size(min = 3, max = 3)
    @Column(nullable = false, name = "alpha3Code", length = 3)
    private String alpha3Code;

    @Column(name = "numericCode", length = 3)
    private String numericCode;

    @Column(name = "region", length = 64)
    private String region;
}