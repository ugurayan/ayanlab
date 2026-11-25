package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.WorkerIncident;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface WorkerIncidentRepository extends JpaRepository<WorkerIncident, UUID> {}