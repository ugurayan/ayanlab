package com.ayanlab.pms.backend.service.impl;

import com.ayanlab.pms.backend.entity.associations.CooperativeShareRecord;
import com.ayanlab.pms.backend.repository.CooperativeShareRecordRepository;
import com.ayanlab.pms.backend.service.ICooperativeShareRecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CooperativeShareRecordService extends AbstractIpmsOrgService<CooperativeShareRecord, CooperativeShareRecordRepository> implements ICooperativeShareRecordService {

    public CooperativeShareRecordService(CooperativeShareRecordRepository repository) {
        super(repository);
    }

    @Override
    protected String entityLabel() {
        return "CooperativeShareRecord";
    }
}
