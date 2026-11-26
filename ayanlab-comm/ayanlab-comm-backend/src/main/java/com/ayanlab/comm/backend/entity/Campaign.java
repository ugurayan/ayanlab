package com.ayanlab.comm.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "campaigns", schema = "comm")
@Getter
@Setter
@NoArgsConstructor
public class Campaign extends BaseEntity {

    @Column(nullable = false, name = "organization_id")
    private UUID organizationId;

    @Column(nullable = false, name = "app_id")
    private UUID appId;

    @Column(nullable = false, name = "name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id")
    private EmailTemplate template;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_list_id")
    private ContactList targetList;

    @Column(length = 50, name = "delivery_mode")
    private String deliveryMode = "RECIPIENT_PREFERRED"; // RECIPIENT_PREFERRED, BILINGUAL_STACK, ALL_STACK

    @Column(length = 50, name = "status")
    private String status = "DRAFT"; // DRAFT, TRANSLATING, READY, SENDING, COMPLETED, FAILED

    @Column(name = "scheduled_at")
    private Instant scheduledAt;

    @Column(name = "sent_at")
    private Instant sentAt;

    @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<CampaignTranslation> translations = new HashSet<>();
}
