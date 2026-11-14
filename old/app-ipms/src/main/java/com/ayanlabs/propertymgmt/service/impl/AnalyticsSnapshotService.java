package com.ayanlabs.propertymgmt.service.impl;

import com.ayanlabs.propertymgmt.entity.reporting.AnalyticsSnapshot;
import com.ayanlabs.propertymgmt.repository.AnalyticsSnapshotRepository;
import com.ayanlabs.propertymgmt.service.IAnalyticsSnapshotService;
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
