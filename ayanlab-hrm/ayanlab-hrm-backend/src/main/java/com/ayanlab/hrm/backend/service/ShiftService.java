package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.Shift;
import com.ayanlab.core.data.CoreContext;
import java.util.List;

public interface ShiftService {
    List<Shift> findAll(CoreContext context);
    Shift findById(String id, CoreContext context);
    Shift create(Shift body, CoreContext context);
    Shift update(Shift body, CoreContext context);
    void deleteById(String id, CoreContext context);
}
