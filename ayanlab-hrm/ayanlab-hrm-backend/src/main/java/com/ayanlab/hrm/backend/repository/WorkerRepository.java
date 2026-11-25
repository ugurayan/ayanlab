package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.Worker;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface WorkerRepository extends JpaRepository<Worker, UUID> {}