package com.ayanlabs.propertymgmt.repository;

import com.ayanlabs.propertymgmt.entity.settings.DataExportJob;
import org.springframework.stereotype.Repository;

@Repository
public interface DataExportJobRepository extends IpmsOrgRepository<DataExportJob, java.util.UUID> {
}
