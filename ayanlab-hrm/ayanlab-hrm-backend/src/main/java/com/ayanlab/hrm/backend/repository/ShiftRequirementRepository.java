package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.ShiftRequirement;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShiftRequirementRepository extends JpaRepository<ShiftRequirement, UUID> {
    List<ShiftRequirement> findByOwnerKindAndOwnerId(String ownerKind, UUID ownerId);

    void deleteByOwnerKindAndOwnerId(String ownerKind, UUID ownerId);
}
