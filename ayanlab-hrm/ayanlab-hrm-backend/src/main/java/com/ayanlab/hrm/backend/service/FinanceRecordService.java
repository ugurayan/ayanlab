package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.FinanceRecord;
import com.ayanlab.core.dto.CoreContext;
import java.util.List;
import java.util.UUID;

public interface FinanceRecordService {
    List<FinanceRecord> findAll(CoreContext context);
    FinanceRecord findById(UUID id, CoreContext context);
    FinanceRecord create(FinanceRecord body, CoreContext context);
    FinanceRecord update(FinanceRecord body, CoreContext context);
    void deleteById(UUID id, CoreContext context);
}
