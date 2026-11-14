package com.ayanlabs.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
    name = "Menu",
    schema = "core",
    uniqueConstraints = @UniqueConstraint(columnNames = {"appId", "code"}),
    indexes = {
        @Index(name = "idx_menu_app", columnList = "appId"),
        @Index(name = "idx_menu_module", columnList = "moduleCode")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Menu extends BaseEntity {

    @Column(nullable = false, name = "appId")
    private UUID appId;

    @NotBlank
    @Column(nullable = false, length = 128)
    private String code;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Column(length = 512)
    private String path;

    @Column(name = "moduleCode", length = 64)
    private String moduleCode;

    @Column(name = "parentCode", length = 128)
    private String parentCode;

    @Column(name = "sortOrder")
    @Builder.Default
    private Integer sortOrder = 0;
}
