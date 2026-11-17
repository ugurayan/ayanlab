package com.ayanlabs.core.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;
import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "Module",
        schema = "core",
        uniqueConstraints = @UniqueConstraint(columnNames = {"code"}))
public class Module extends BaseEntity {

    @Column(nullable = false, name = "code")
    @NotBlank
    private String code;

    @Column(nullable = false, name = "name")
    @NotBlank
    private String name;

    private String description;

//    @OneToMany(mappedBy = "module", fetch = FetchType.LAZY)
//    @Builder.Default
//    private Set<ApplicationModule> applicationModules = new HashSet<>();
}
