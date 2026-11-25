package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.Shift;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShiftRepository extends JpaRepository<Shift, UUID> {
    List<Shift> findByPlatformOrganizationId(UUID platformOrganizationId);

    List<Shift> findByTemplateId(UUID templateId);

    Optional<Shift> findByTemplateIdAndLocationIdAndStartsAt(UUID templateId, UUID locationId, Instant startsAt);

    boolean existsByLocationId(UUID locationId);

    boolean existsByPlatformOrganizationId(UUID platformOrganizationId);
}
