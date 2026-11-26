package com.ayanlab.comm.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "campaign_translations", schema = "comm", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"campaign_id", "language"})
})
@Getter
@Setter
@NoArgsConstructor
public class CampaignTranslation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id", nullable = false)
    @JsonIgnore
    private Campaign campaign;

    @Column(nullable = false, length = 10, name = "language")
    private String language;

    @Column(nullable = false, name = "subject")
    private String subject;

    @Column(nullable = false, columnDefinition = "text", name = "body_html")
    private String bodyHtml;

    @Column(length = 50, name = "status")
    private String status = "DRAFT"; // DRAFT, APPROVED
}
