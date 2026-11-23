package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.ProfileContact;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileContactRepository extends JpaRepository<ProfileContact, String> {
    List<ProfileContact> findByProfileId(String profileId);
    List<ProfileContact> findByOrgId(String orgId);
    List<ProfileContact> findByWorkerId(String workerId);
}
