package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.FinanceRecord;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface FinanceRecordRepository extends JpaRepository<FinanceRecord, UUID> {
    List<FinanceRecord> findByProfileId(UUID profileId);
    List<FinanceRecord> findByOrgId(UUID orgId);
    List<FinanceRecord> findByWorkerId(UUID workerId);
}
