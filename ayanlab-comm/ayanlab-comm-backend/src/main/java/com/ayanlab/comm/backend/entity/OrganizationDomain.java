package com.ayanlab.comm.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "organization_domains", schema = "comm")
@Getter
@Setter
@NoArgsConstructor
public class OrganizationDomain extends BaseEntity {

    @Column(nullable = false, name = "organization_id")
    private UUID organizationId;

    @Column(nullable = false, name = "app_id")
    private UUID appId;

    @Column(nullable = false, unique = true, name = "domain_name")
    private String domainName;

    @Column(nullable = false, name = "verification_token")
    private String verificationToken;

    @Column(name = "dkim_selector")
    private String dkimSelector = "ayan";

    @Column(name = "dkim_public_key", columnDefinition = "text")
    private String dkimPublicKey;

    @Column(name = "dkim_private_key", columnDefinition = "text")
    private String dkimPrivateKey;

    @Column(name = "spf_verified")
    private Boolean spfVerified = false;

    @Column(name = "dkim_verified")
    private Boolean dkimVerified = false;

    @Column(name = "dmarc_verified")
    private Boolean dmarcVerified = false;

    @Column(length = 50, name = "status")
    private String status = "PENDING"; // PENDING, VERIFIED, FAILED
}
