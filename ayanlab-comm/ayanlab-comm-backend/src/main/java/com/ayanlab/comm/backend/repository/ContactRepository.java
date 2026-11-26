package com.ayanlab.comm.backend.repository;

import com.ayanlab.comm.backend.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContactRepository extends JpaRepository<Contact, UUID> {
    Optional<Contact> findByOrganizationIdAndEmail(UUID organizationId, String email);
    List<Contact> findByOrganizationId(UUID organizationId);
    List<Contact> findByOrganizationIdAndStatus(UUID organizationId, String status);
}
