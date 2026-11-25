package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.TransactionRecord;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface TransactionRecordRepository extends JpaRepository<TransactionRecord, UUID> {
    List<TransactionRecord> findByProfileId(UUID profileId);
    List<TransactionRecord> findByOrgId(UUID orgId);
    List<TransactionRecord> findByWorkerId(UUID workerId);
}
