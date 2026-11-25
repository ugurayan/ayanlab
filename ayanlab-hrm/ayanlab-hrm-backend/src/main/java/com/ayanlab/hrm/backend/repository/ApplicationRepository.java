package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.Application;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ApplicationRepository extends JpaRepository<Application, UUID> {
    List<Application> findByWorkerId(UUID workerId);
}