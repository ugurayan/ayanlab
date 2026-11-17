package com.ayanlabs.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "TwilioSmsLog", schema = "core")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TwilioSmsLog extends BaseEntity {

    @Column(nullable = false)
    private String toNumber;

    @Column(nullable = false)
    private String message;
}

