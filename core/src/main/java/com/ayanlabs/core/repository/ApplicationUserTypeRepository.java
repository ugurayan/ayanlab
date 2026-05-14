package com.ayanlabs.core.repository;

import com.ayanlabs.core.entity.ApplicationUserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ApplicationUserTypeRepository extends JpaRepository<ApplicationUserType, UUID> {
    boolean existsByAppIdAndUserTypeId(UUID appId, UUID userTypeId);
}
