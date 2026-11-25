package com.ayanlab.hrm.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

import com.ayanlab.hrm.backend.enums.ReferenceEntityKind;
@Getter
@Setter
@Entity
@Table(name = "Location")
public class Location extends BaseEntity {

    @Column(name = "referenceId")
    private UUID referenceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "referenceEntity")
    private ReferenceEntityKind referenceEntity;

    @Column(name = "placeId")
    private String placeId;

    private String name;

    @Column(name = "formattedAddress")
    private String formattedAddress;

    private String street;
    private String city;
    private String state;

    @Column(name = "zipCode")
    private String zipCode;

    private String country;
    private Double latitude;
    private Double longitude;

    @Column(columnDefinition = "text")
    private String notes;

    private String timezone;
}
