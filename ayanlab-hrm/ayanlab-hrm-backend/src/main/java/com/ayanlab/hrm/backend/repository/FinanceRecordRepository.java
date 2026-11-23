package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.FinanceRecord;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FinanceRecordRepository extends JpaRepository<FinanceRecord, String> {
    List<FinanceRecord> findByProfileId(String profileId);
    List<FinanceRecord> findByOrgId(String orgId);
    List<FinanceRecord> findByWorkerId(String workerId);
}
