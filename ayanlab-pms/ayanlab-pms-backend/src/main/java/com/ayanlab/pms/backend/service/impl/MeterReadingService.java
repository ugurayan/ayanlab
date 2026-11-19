package com.ayanlab.pms.backend.service.impl;

import com.ayanlab.pms.backend.entity.tr.MeterReading;
import com.ayanlab.pms.backend.repository.MeterReadingRepository;
import com.ayanlab.pms.backend.service.IMeterReadingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MeterReadingService extends AbstractIpmsOrgService<MeterReading, MeterReadingRepository> implements IMeterReadingService {

    public MeterReadingService(MeterReadingRepository repository) {
        super(repository);
    }

    @Override
    protected String entityLabel() {
        return "MeterReading";
    }
}
