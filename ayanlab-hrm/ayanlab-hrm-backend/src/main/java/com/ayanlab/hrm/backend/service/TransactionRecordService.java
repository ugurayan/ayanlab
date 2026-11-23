package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.TransactionRecord;
import com.ayanlab.core.data.CoreContext;
import java.util.List;

public interface TransactionRecordService {
    List<TransactionRecord> findAll(CoreContext context);
    TransactionRecord findById(String id, CoreContext context);
    TransactionRecord create(TransactionRecord body, CoreContext context);
    TransactionRecord update(TransactionRecord body, CoreContext context);
    void deleteById(String id, CoreContext context);
}
