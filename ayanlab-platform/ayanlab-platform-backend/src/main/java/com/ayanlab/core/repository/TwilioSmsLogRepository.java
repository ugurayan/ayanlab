package com.ayanlab.core.repository;

import com.ayanlab.core.entity.TwilioSmsLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface TwilioSmsLogRepository extends JpaRepository<TwilioSmsLog, UUID> {
    boolean existsByToNumberAndMessage(String toNumber, String message);
}

