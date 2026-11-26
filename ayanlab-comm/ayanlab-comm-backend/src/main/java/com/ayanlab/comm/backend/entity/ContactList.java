package com.ayanlab.comm.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "contact_lists", schema = "comm")
@Getter
@Setter
@NoArgsConstructor
public class ContactList extends BaseEntity {

    @Column(nullable = false, name = "organization_id")
    private UUID organizationId;

    @Column(nullable = false, name = "app_id")
    private UUID appId;

    @Column(nullable = false, name = "name")
    private String name;

    @Column(columnDefinition = "text", name = "description")
    private String description;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "contact_list_members",
        schema = "comm",
        joinColumns = @JoinColumn(name = "list_id"),
        inverseJoinColumns = @JoinColumn(name = "contact_id")
    )
    private Set<Contact> contacts = new HashSet<>();
}
