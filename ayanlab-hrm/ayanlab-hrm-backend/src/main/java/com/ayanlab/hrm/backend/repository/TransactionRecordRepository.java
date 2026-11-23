package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.TransactionRecord;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRecordRepository extends JpaRepository<TransactionRecord, String> {
    List<TransactionRecord> findByProfileId(String profileId);
    List<TransactionRecord> findByOrgId(String orgId);
    List<TransactionRecord> findByWorkerId(String workerId);
}
