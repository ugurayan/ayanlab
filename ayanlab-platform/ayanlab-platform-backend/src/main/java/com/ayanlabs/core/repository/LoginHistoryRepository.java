package com.ayanlabs.core.repository;

import com.ayanlabs.core.entity.LoginHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LoginHistoryRepository extends JpaRepository<LoginHistory, UUID> {
    List<LoginHistory> findByUserIdOrderByLoginAtDesc(UUID userId);
}