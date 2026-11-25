package com.ayanlab.pms.backend.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.dto.ClientHeadersPayload;
import com.ayanlab.core.dto.response.ApiResponse;
import com.ayanlab.pms.backend.entity.tasks.RecurringTask;
import com.ayanlab.pms.backend.service.IRecurringTaskService;
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
@RequestMapping("/pms/api/recurring-tasks")
@Tag(name = "IPMS · RecurringTaskController", description = "RecurringTask CRUD")
@RequiredArgsConstructor
public class RecurringTaskController extends IpmsCrudController<RecurringTask> {

    private static final String BASE_PATH = "/pms/api/recurring-tasks";
    private final IRecurringTaskService recurringTaskService;

    @Override
    protected IRecurringTaskService service() {
        return recurringTaskService;
    }

    @Override
    protected String basePath() {
        return BASE_PATH;
    }

    @Override
    protected String operationPrefix() {
        return "recurring-tasks";
    }

    @Override
    protected String entityLabel() {
        return "RecurringTask";
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<RecurringTask>>> list(
            @ClientHeaders ClientHeadersPayload headers) {
        return executeList(headers, Map.of(), service()::findAll);
    }
}
