package com.ayanlab.pms.backend.service.impl;

import com.ayanlab.pms.backend.entity.tr.PatrolCheckpoint;
import com.ayanlab.pms.backend.repository.PatrolCheckpointRepository;
import com.ayanlab.pms.backend.service.IPatrolCheckpointService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PatrolCheckpointService extends AbstractIpmsOrgService<PatrolCheckpoint, PatrolCheckpointRepository> implements IPatrolCheckpointService {

    public PatrolCheckpointService(PatrolCheckpointRepository repository) {
        super(repository);
    }

    @Override
    protected String entityLabel() {
        return "PatrolCheckpoint";
    }
}
