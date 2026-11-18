package com.ayanlab.pms.backend.repository;

import com.ayanlab.pms.backend.entity.settings.DataExportJob;
import org.springframework.stereotype.Repository;

@Repository
public interface DataExportJobRepository extends IpmsOrgRepository<DataExportJob, java.util.UUID> {
}
