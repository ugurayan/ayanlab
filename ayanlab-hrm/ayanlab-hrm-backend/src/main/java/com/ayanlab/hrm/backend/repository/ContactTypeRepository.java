package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.ContactType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactTypeRepository extends JpaRepository<ContactType, String> {
    List<ContactType> findByProfileId(String profileId);
    List<ContactType> findByOrgId(String orgId);
    List<ContactType> findByWorkerId(String workerId);
}
