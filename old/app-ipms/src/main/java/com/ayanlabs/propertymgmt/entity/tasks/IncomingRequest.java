package com.ayanlabs.propertymgmt.entity.tasks;

import com.ayanlabs.core.entity.BaseEntity;
import com.ayanlabs.propertymgmt.data.OrgScoped;
import com.ayanlabs.propertymgmt.data.enums.IncomingRequestStatusEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
    name = "IncomingRequest",
    schema = "ipms",
    indexes = {
        @Index(name = "idx_ipms_incoming_org", columnList = "organizationId"),
        @Index(name = "idx_ipms_incoming_task", columnList = "assignedTaskId")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncomingRequest extends BaseEntity implements OrgScoped {

    @Column(nullable = false, name = "organizationId")
    private UUID organizationId;

    @Column(nullable = false, name = "sourceType")
    private String sourceType;

    @Column(name = "sourceProfileId")
    private UUID sourceProfileId;

    @NotBlank
    @Column(nullable = false)
    private String subject;

    @Column(columnDefinition = "text")
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private IncomingRequestStatusEnum status = IncomingRequestStatusEnum.NEW;

    @Column(name = "assignedTaskId")
    private UUID assignedTaskId;

    @Column(name = "receivedAt")
    private Instant receivedAt;
}
