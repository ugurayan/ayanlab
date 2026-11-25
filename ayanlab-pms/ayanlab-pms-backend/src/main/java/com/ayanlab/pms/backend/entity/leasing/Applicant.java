package com.ayanlab.pms.backend.entity.leasing;

import com.ayanlab.core.entity.BaseEntity;
import com.ayanlab.pms.backend.dto.OrgScoped;
import com.ayanlab.pms.backend.enums.ApplicantStatusEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
    name = "Applicant",
    schema = "ipms",
    indexes = {
        @Index(name = "idx_ipms_applicant_org", columnList = "organizationId"),
        @Index(name = "idx_ipms_applicant_listing", columnList = "listingId")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Applicant extends BaseEntity implements OrgScoped {

    @Column(nullable = false, name = "organizationId")
    private UUID organizationId;

    @Column(nullable = false, name = "listingId")
    private UUID listingId;

    @NotBlank
    @Column(nullable = false, name = "firstName")
    private String firstName;

    @NotBlank
    @Column(nullable = false, name = "lastName")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ApplicantStatusEnum status = ApplicantStatusEnum.NEW;

    @Column(name = "appliedAt")
    private Instant appliedAt;

    @Column(name = "screeningNotes", columnDefinition = "text")
    private String screeningNotes;
}
