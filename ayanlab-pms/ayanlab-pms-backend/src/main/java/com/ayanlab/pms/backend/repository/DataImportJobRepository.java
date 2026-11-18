package com.ayanlab.pms.backend.repository;

import com.ayanlab.pms.backend.entity.settings.DataImportJob;
import org.springframework.stereotype.Repository;

@Repository
public interface DataImportJobRepository extends IpmsOrgRepository<DataImportJob, java.util.UUID> {
}
