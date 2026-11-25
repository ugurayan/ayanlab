package com.ayanlab.hrm.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "ShiftRequirement")
public class ShiftRequirement extends BaseEntity {

    @Column(name = "ownerId")
    private UUID ownerId;

    /** TEMPLATE or INSTANCE */
    @Column(name = "ownerKind")
    private String ownerKind;

    /** SKILL, LANGUAGE, CERTIFICATE, DOCUMENT, or CONTACT */
    private String kind;

    @Column(name = "skillId")
    private UUID skillId;

    @Column(name = "expertiseLevel")
    private String expertiseLevel;

    @Column(name = "experienceDuration")
    private String experienceDuration;

    private String language;
    private String level;

    @Column(name = "certificationNeeded")
    private Boolean certificationNeeded;

    @Column(name = "certificateId")
    private UUID certificateId;

    @Column(name = "customName")
    private String customName;

    @Column(name = "documentId")
    private UUID documentId;

    @Column(name = "contactId")
    private UUID contactId;
}
