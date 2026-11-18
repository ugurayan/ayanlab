package com.ayanlab.pms.backend.service.impl;

import com.ayanlab.pms.backend.entity.reporting.ReportDefinition;
import com.ayanlab.pms.backend.repository.ReportDefinitionRepository;
import com.ayanlab.pms.backend.service.IReportDefinitionService;
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
