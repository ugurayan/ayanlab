package com.ayanlab.hrm.backend.service;

import com.ayanlab.core.dto.CoreContext;
import com.ayanlab.hrm.backend.dto.request.GenerateShiftInstancesRequest;
import com.ayanlab.hrm.backend.entity.Shift;
import com.ayanlab.hrm.backend.entity.ShiftTemplate;
import java.util.List;
import java.util.UUID;

public interface ShiftTemplateService {
    List<ShiftTemplate> findAll(CoreContext context);
    ShiftTemplate findById(UUID id, CoreContext context);
    ShiftTemplate create(ShiftTemplate body, CoreContext context);
    ShiftTemplate update(ShiftTemplate body, CoreContext context);
    void deleteById(UUID id, CoreContext context);
    List<Shift> generate(UUID id, GenerateShiftInstancesRequest request, CoreContext context);
}
