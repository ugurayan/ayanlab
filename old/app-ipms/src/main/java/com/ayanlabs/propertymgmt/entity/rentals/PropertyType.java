package com.ayanlabs.propertymgmt.entity.rentals;

import com.ayanlabs.core.entity.BaseEntity;
import com.ayanlabs.propertymgmt.data.OrgScoped;
import com.ayanlabs.propertymgmt.data.enums.PropertyTypeCategoryEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
    name = "PropertyType",
    schema = "ipms",
    uniqueConstraints = @UniqueConstraint(columnNames = {"organizationId", "code"}),
    indexes = {
        @Index(name = "idx_ipms_property_type_org", columnList = "organizationId"),
        @Index(name = "idx_ipms_property_type_code", columnList = "code")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropertyType extends BaseEntity implements OrgScoped {

    /** Null = system-wide default visible to all property management organizations. */
    @Column(name = "organizationId")
    private UUID organizationId;

    @NotBlank
    @Column(nullable = false)
    private String code;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PropertyTypeCategoryEnum category;

    @Column(name = "sortOrder")
    @Builder.Default
    private Integer sortOrder = 0;

    @Column(name = "isSystemDefault", nullable = false)
    @Builder.Default
    private Boolean isSystemDefault = false;

    public String getCode() {
        return code;
    }

    public Boolean getIsSystemDefault() {
        return isSystemDefault;
    }

    public void setIsSystemDefault(Boolean isSystemDefault) {
        this.isSystemDefault = isSystemDefault;
    }
}
