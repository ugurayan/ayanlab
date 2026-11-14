package com.ayanlabs.propertymgmt.service.impl;

import com.ayanlabs.propertymgmt.entity.settings.DataExportJob;
import com.ayanlabs.propertymgmt.repository.DataExportJobRepository;
import com.ayanlabs.propertymgmt.service.IDataExportJobService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DataExportJobService extends AbstractIpmsOrgService<DataExportJob, DataExportJobRepository> implements IDataExportJobService {

    public DataExportJobService(DataExportJobRepository repository) {
        super(repository);
    }

    @Override
    protected String entityLabel() {
        return "DataExportJob";
    }
}
