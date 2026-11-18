package com.ayanlab.pms.backend.service.impl;

import com.ayanlab.pms.backend.entity.reporting.AnalyticsSnapshot;
import com.ayanlab.pms.backend.repository.AnalyticsSnapshotRepository;
import com.ayanlab.pms.backend.service.IAnalyticsSnapshotService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AnalyticsSnapshotService extends AbstractIpmsOrgService<AnalyticsSnapshot, AnalyticsSnapshotRepository> implements IAnalyticsSnapshotService {

    public AnalyticsSnapshotService(AnalyticsSnapshotRepository repository) {
        super(repository);
    }

    @Override
    protected String entityLabel() {
        return "AnalyticsSnapshot";
    }
}
