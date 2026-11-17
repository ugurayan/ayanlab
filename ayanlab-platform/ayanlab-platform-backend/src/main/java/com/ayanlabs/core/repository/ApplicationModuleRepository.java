package com.ayanlabs.core.repository;

import com.ayanlabs.core.entity.ApplicationModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ApplicationModuleRepository extends JpaRepository<ApplicationModule, UUID> {
    boolean existsByAppIdAndModuleId(UUID appId, UUID moduleId);

    List<ApplicationModule> findByIsActiveTrue();

    List<ApplicationModule> findByAppId(UUID appId);

    List<ApplicationModule> findByAppIdAndIsActiveTrue(UUID appId);
}
