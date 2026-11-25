package com.ayanlab.pms.backend.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.dto.ClientHeadersPayload;
import com.ayanlab.core.dto.response.ApiResponse;
import com.ayanlab.pms.backend.entity.tr.StaffTimesheet;
import com.ayanlab.pms.backend.service.IStaffTimesheetService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pms/api/staff-timesheets")
@Tag(name = "IPMS · StaffTimesheetController", description = "Staff timesheet (TR module CRUD)")
@RequiredArgsConstructor
public class StaffTimesheetController extends IpmsCrudController<StaffTimesheet> {

    private static final String BASE_PATH = "/pms/api/staff-timesheets";
    private final IStaffTimesheetService staffTimesheetService;

    @Override
    protected IStaffTimesheetService service() {
        return staffTimesheetService;
    }

    @Override
    protected String basePath() {
        return BASE_PATH;
    }

    @Override
    protected String operationPrefix() {
        return "staff-timesheets";
    }

    @Override
    protected String entityLabel() {
        return "StaffTimesheet";
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<StaffTimesheet>>> list(@ClientHeaders ClientHeadersPayload headers) {
        return executeList(headers, Map.of(), service()::findAll);
    }
}
