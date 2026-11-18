package com.ayanlab.pms.backend.repository;

import com.ayanlab.pms.backend.entity.tasks.RecurringTask;
import org.springframework.stereotype.Repository;

@Repository
public interface RecurringTaskRepository extends IpmsOrgRepository<RecurringTask, java.util.UUID> {
}
