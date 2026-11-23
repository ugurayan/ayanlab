package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.MockRecord;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MockRecordRepository extends JpaRepository<MockRecord, String> {
    List<MockRecord> findByCollection(String collection);
}