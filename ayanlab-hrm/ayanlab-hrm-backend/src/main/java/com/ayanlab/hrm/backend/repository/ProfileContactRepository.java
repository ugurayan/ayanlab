package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.ProfileContact;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ProfileContactRepository extends JpaRepository<ProfileContact, UUID> {
    List<ProfileContact> findByProfileId(UUID profileId);
    List<ProfileContact> findByOrgId(UUID orgId);
    List<ProfileContact> findByWorkerId(UUID workerId);
}
