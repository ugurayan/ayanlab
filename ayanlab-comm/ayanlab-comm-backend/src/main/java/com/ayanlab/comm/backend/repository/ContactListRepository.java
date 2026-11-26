package com.ayanlab.comm.backend.repository;

import com.ayanlab.comm.backend.entity.ContactList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ContactListRepository extends JpaRepository<ContactList, UUID> {
    List<ContactList> findByOrganizationId(UUID organizationId);
}
