package com.ayanlab.comm.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "email_template_translations", schema = "comm", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"template_id", "language"})
})
@Getter
@Setter
@NoArgsConstructor
public class EmailTemplateTranslation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    @JsonIgnore
    private EmailTemplate template;

    @Column(nullable = false, length = 10, name = "language")
    private String language;

    @Column(nullable = false, name = "subject")
    private String subject;

    @Column(nullable = false, columnDefinition = "text", name = "body_html")
    private String bodyHtml;

    @Column(length = 50, name = "status")
    private String status = "DRAFT"; // DRAFT, AUTO_TRANSLATED, APPROVED

    @Column(name = "approved_by")
    private String approvedBy;

    @Column(name = "approved_at")
    private Instant approvedAt;
}
