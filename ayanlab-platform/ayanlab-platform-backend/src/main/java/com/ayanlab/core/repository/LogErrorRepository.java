package com.ayanlab.core.repository;

import com.ayanlab.core.entity.LogError;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LogErrorRepository extends JpaRepository<LogError, UUID> {
    List<LogError> findByStatusOrderByCreatedAtDesc(String status);
}