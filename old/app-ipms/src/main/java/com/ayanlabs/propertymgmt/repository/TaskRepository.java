package com.ayanlabs.propertymgmt.repository;

import com.ayanlabs.propertymgmt.entity.tasks.Task;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends IpmsOrgRepository<Task, java.util.UUID> {
}
