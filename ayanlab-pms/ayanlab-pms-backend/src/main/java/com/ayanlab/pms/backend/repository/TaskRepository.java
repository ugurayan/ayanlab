package com.ayanlab.pms.backend.repository;

import com.ayanlab.pms.backend.entity.tasks.Task;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends IpmsOrgRepository<Task, java.util.UUID> {
}
