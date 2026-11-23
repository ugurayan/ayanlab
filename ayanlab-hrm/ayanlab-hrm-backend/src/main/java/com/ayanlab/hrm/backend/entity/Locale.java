package com.ayanlab.hrm.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "Locale")
public class Locale {

    @Id
    @Column(length = 8)
    private String code;

    /** Label shown in the language picker (typically English). */
    private String displayName;

    /** BCP-47 style tag when different from code (e.g. zh → zh-CN). */
    @Column(length = 16)
    private String bcp47Tag;

    private int sortOrder;

    private boolean enabled = true;
}