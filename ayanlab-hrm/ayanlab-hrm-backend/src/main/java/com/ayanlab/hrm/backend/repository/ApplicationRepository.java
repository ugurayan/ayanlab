package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.Application;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<Application, String> {
    List<Application> findByWorkerId(String workerId);
}