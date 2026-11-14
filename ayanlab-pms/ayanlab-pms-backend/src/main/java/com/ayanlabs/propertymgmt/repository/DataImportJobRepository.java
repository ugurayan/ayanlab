package com.ayanlabs.propertymgmt.repository;

import com.ayanlabs.propertymgmt.entity.settings.DataImportJob;
import org.springframework.stereotype.Repository;

@Repository
public interface DataImportJobRepository extends IpmsOrgRepository<DataImportJob, java.util.UUID> {
}
