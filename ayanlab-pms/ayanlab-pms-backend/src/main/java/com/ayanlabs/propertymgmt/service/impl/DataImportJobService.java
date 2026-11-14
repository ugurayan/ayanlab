package com.ayanlabs.propertymgmt.service.impl;

import com.ayanlabs.propertymgmt.entity.settings.DataImportJob;
import com.ayanlabs.propertymgmt.repository.DataImportJobRepository;
import com.ayanlabs.propertymgmt.service.IDataImportJobService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DataImportJobService extends AbstractIpmsOrgService<DataImportJob, DataImportJobRepository> implements IDataImportJobService {

    public DataImportJobService(DataImportJobRepository repository) {
        super(repository);
    }

    @Override
    protected String entityLabel() {
        return "DataImportJob";
    }
}
