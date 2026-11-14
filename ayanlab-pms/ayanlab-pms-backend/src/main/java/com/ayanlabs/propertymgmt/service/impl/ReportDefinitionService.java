package com.ayanlabs.propertymgmt.service.impl;

import com.ayanlabs.propertymgmt.entity.reporting.ReportDefinition;
import com.ayanlabs.propertymgmt.repository.ReportDefinitionRepository;
import com.ayanlabs.propertymgmt.service.IReportDefinitionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ReportDefinitionService extends AbstractIpmsOrgService<ReportDefinition, ReportDefinitionRepository> implements IReportDefinitionService {

    public ReportDefinitionService(ReportDefinitionRepository repository) {
        super(repository);
    }

    @Override
    protected String entityLabel() {
        return "ReportDefinition";
    }
}
