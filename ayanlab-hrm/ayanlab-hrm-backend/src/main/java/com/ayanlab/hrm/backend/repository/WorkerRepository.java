package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.Worker;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkerRepository extends JpaRepository<Worker, String> {}