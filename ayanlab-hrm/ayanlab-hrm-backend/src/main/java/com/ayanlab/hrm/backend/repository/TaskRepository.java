package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.Task;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {
    List<Task> findByProfileId(UUID profileId);
    List<Task> findByOrgId(UUID orgId);
    List<Task> findByWorkerId(UUID workerId);
}
