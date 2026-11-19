package com.ayanlab.pms.backend.service.impl;

import com.ayanlab.pms.backend.entity.tr.PatrolRoute;
import com.ayanlab.pms.backend.repository.PatrolRouteRepository;
import com.ayanlab.pms.backend.service.IPatrolRouteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PatrolRouteService extends AbstractIpmsOrgService<PatrolRoute, PatrolRouteRepository> implements IPatrolRouteService {

    public PatrolRouteService(PatrolRouteRepository repository) {
        super(repository);
    }

    @Override
    protected String entityLabel() {
        return "PatrolRoute";
    }
}
