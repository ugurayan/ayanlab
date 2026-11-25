package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.Location;
import com.ayanlab.core.dto.CoreContext;
import java.util.List;
import java.util.UUID;

public interface LocationService {
    List<Location> findAll(CoreContext context, String referenceEntity, UUID referenceId);
    Location findById(UUID id, CoreContext context);
    Location create(Location body, CoreContext context);
    Location update(Location body, CoreContext context);
    void deleteById(UUID id, CoreContext context);
}
