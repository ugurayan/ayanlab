package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.Location;
import com.ayanlab.core.data.CoreContext;
import java.util.List;

public interface LocationService {
    List<Location> findAll(CoreContext context);
    Location findById(String id, CoreContext context);
    Location create(Location body, CoreContext context);
    Location update(Location body, CoreContext context);
    void deleteById(String id, CoreContext context);
}
