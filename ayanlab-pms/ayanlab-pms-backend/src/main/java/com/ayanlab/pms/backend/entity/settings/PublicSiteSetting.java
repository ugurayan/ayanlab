package com.ayanlab.pms.backend.entity.settings;

import com.ayanlab.core.entity.BaseEntity;
import com.ayanlab.pms.backend.dto.OrgScoped;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
    name = "PublicSiteSetting",
    schema = "ipms",
    indexes = @Index(name = "idx_ipms_publicsitesetting_org", columnList = "organizationId")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublicSiteSetting extends BaseEntity implements OrgScoped {

    @Column(nullable = false, name = "organizationId")
    private UUID organizationId;

    @Column(name = "websiteUrl")
    private String websiteUrl;

    @Column(name = "brandingMode")
    private String brandingMode;

    @Column(name = "linkText")
    private String linkText;

    @Column(name = "linkUrl")
    private String linkUrl;

    @Column(name = "allowTenantSignIn")
    @Builder.Default
    private Boolean allowTenantSignIn = true;

    @Column(name = "allowOnlineRegister")
    @Builder.Default
    private Boolean allowOnlineRegister = true;

    @Column(name = "allowOwnerVendorStaffSignIn")
    @Builder.Default
    private Boolean allowOwnerVendorStaffSignIn = true;

    @Column(name = "streetAddress")
    private String streetAddress;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "zipCode")
    private String zipCode;

    @Column(name = "status")
    @Builder.Default
    private String status = "ACTIVE";

    @Column(name = "metadataJson", columnDefinition = "text")
    private String metadataJson;

}
