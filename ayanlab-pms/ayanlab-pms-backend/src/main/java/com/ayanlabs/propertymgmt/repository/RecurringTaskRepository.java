package com.ayanlabs.propertymgmt.repository;

import com.ayanlabs.propertymgmt.entity.tasks.RecurringTask;
import org.springframework.stereotype.Repository;

@Repository
public interface RecurringTaskRepository extends IpmsOrgRepository<RecurringTask, java.util.UUID> {
}
