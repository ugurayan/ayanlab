package com.ayanlab.pms.backend.service.impl;

import com.ayanlab.pms.backend.entity.settings.DataExportJob;
import com.ayanlab.pms.backend.repository.DataExportJobRepository;
import com.ayanlab.pms.backend.service.IDataExportJobService;
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
