package com.ayanlab.pms.backend.service.impl;

import com.ayanlab.pms.backend.entity.tr.StaffTimesheet;
import com.ayanlab.pms.backend.repository.StaffTimesheetRepository;
import com.ayanlab.pms.backend.service.IStaffTimesheetService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StaffTimesheetService extends AbstractIpmsOrgService<StaffTimesheet, StaffTimesheetRepository> implements IStaffTimesheetService {

    public StaffTimesheetService(StaffTimesheetRepository repository) {
        super(repository);
    }

    @Override
    protected String entityLabel() {
        return "StaffTimesheet";
    }
}
