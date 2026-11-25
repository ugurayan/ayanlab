package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.Location;
import com.ayanlab.hrm.backend.enums.ReferenceEntityKind;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, UUID> {
    List<Location> findByReferenceEntityAndReferenceId(ReferenceEntityKind referenceEntity, UUID referenceId);

    List<Location> findByReferenceEntityAndReferenceIdIn(ReferenceEntityKind referenceEntity, Collection<UUID> referenceIds);

    boolean existsByReferenceEntityAndReferenceId(ReferenceEntityKind referenceEntity, UUID referenceId);
}
