package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.ProfileLocationDetail;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileLocationDetailRepository extends JpaRepository<ProfileLocationDetail, String> {
    List<ProfileLocationDetail> findByProfileId(String profileId);
    List<ProfileLocationDetail> findByOrgId(String orgId);
    List<ProfileLocationDetail> findByWorkerId(String workerId);
}
