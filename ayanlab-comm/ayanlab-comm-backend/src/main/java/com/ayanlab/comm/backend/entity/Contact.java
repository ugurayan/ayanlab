package com.ayanlab.comm.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "contacts", schema = "comm", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"organization_id", "email"})
})
@Getter
@Setter
@NoArgsConstructor
public class Contact extends BaseEntity {

    @Column(nullable = false, name = "organization_id")
    private UUID organizationId;

    @Column(nullable = false, name = "app_id")
    private UUID appId;

    @Column(nullable = false, name = "email")
    private String email;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "preferred_language", length = 10)
    private String preferredLanguage = "en";

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "custom_attributes")
    private Map<String, Object> customAttributes;

    @Column(length = 50, name = "status")
    private String status = "SUBSCRIBED"; // SUBSCRIBED, UNSUBSCRIBED, BOUNCED

    @Column(name = "external_id")
    private String externalId;
}
