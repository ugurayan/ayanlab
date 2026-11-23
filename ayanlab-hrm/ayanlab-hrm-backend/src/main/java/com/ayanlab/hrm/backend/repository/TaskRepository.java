package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.Task;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, String> {
    List<Task> findByProfileId(String profileId);
    List<Task> findByOrgId(String orgId);
    List<Task> findByWorkerId(String workerId);
}
