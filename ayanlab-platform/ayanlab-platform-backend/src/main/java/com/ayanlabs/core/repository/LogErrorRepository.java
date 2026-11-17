package com.ayanlabs.core.repository;

import com.ayanlabs.core.entity.LogError;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LogErrorRepository extends JpaRepository<LogError, UUID> {
    List<LogError> findByStatusOrderByCreatedAtDesc(String status);
}