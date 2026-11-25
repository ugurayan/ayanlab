package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.ProfileLocationDetail;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ProfileLocationDetailRepository extends JpaRepository<ProfileLocationDetail, UUID> {
    List<ProfileLocationDetail> findByProfileId(UUID profileId);
    List<ProfileLocationDetail> findByOrgId(UUID orgId);
    List<ProfileLocationDetail> findByWorkerId(UUID workerId);
}
