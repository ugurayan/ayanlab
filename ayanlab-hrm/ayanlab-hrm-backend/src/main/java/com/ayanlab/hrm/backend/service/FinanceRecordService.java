package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.FinanceRecord;
import com.ayanlab.core.data.CoreContext;
import java.util.List;

public interface FinanceRecordService {
    List<FinanceRecord> findAll(CoreContext context);
    FinanceRecord findById(String id, CoreContext context);
    FinanceRecord create(FinanceRecord body, CoreContext context);
    FinanceRecord update(FinanceRecord body, CoreContext context);
    void deleteById(String id, CoreContext context);
}
