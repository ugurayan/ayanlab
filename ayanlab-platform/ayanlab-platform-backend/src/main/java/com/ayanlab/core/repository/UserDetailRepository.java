package com.ayanlab.core.repository;

import com.ayanlab.core.entity.UserDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserDetailRepository extends JpaRepository<UserDetail, UUID> {
    boolean existsByProfileIdAndDetailKey(UUID profileId, String detailKey);
}
