package com.ayanlab.core.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
    name = "RoleMenu",
    schema = "core",
    uniqueConstraints = @UniqueConstraint(columnNames = {"roleId", "menuId"}),
    indexes = {
        @Index(name = "idx_role_menu_role", columnList = "roleId"),
        @Index(name = "idx_role_menu_menu", columnList = "menuId")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleMenu extends BaseEntity {

    @Column(nullable = false, name = "roleId")
    private UUID roleId;

    @Column(nullable = false, name = "menuId")
    private UUID menuId;
}
