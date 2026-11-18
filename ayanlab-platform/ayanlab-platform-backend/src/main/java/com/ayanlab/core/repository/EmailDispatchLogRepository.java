package com.ayanlab.core.repository;

import com.ayanlab.core.entity.EmailDispatchLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EmailDispatchLogRepository extends JpaRepository<EmailDispatchLog, UUID> {
}

