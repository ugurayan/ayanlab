package com.ayanlab.pms.backend.service.impl;

import com.ayanlab.pms.backend.entity.settings.DataImportJob;
import com.ayanlab.pms.backend.repository.DataImportJobRepository;
import com.ayanlab.pms.backend.service.IDataImportJobService;
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
