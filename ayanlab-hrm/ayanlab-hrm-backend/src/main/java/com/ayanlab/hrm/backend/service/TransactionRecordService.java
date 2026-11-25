package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.TransactionRecord;
import com.ayanlab.core.dto.CoreContext;
import java.util.List;
import java.util.UUID;

public interface TransactionRecordService {
    List<TransactionRecord> findAll(CoreContext context);
    TransactionRecord findById(UUID id, CoreContext context);
    TransactionRecord create(TransactionRecord body, CoreContext context);
    TransactionRecord update(TransactionRecord body, CoreContext context);
    void deleteById(UUID id, CoreContext context);
}
