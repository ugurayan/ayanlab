package com.ayanlab.pms.backend.repository;

import com.ayanlab.pms.backend.entity.tr.StaffTimesheet;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffTimesheetRepository extends IpmsOrgRepository<StaffTimesheet, java.util.UUID> {
}
