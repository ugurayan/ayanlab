package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.Shift;
import com.ayanlab.core.dto.CoreContext;
import java.util.List;
import java.util.UUID;

public interface ShiftService {
    List<Shift> findAll(CoreContext context);
    Shift findById(UUID id, CoreContext context);
    Shift create(Shift body, CoreContext context);
    Shift update(Shift body, CoreContext context);
    void deleteById(UUID id, CoreContext context);
}
