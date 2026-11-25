package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.ContactType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ContactTypeRepository extends JpaRepository<ContactType, UUID> {
    List<ContactType> findByProfileId(UUID profileId);
    List<ContactType> findByOrgId(UUID orgId);
    List<ContactType> findByWorkerId(UUID workerId);
}
