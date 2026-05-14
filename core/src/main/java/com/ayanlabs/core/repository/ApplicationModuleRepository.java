package com.ayanlabs.core.repository;

import com.ayanlabs.core.entity.ApplicationModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ApplicationModuleRepository extends JpaRepository<ApplicationModule, UUID> {
    boolean existsByAppIdAndModuleId(UUID appId, UUID moduleId);
}
