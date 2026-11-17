package com.ayanlabs.propertymgmt.controller;

import com.ayanlabs.propertymgmt.entity.tasks.Task;
import com.ayanlabs.propertymgmt.service.ITaskService;

import com.ayanlabs.core.annotation.ClientHeaders;
import com.ayanlabs.core.data.ClientHeadersPayload;
import com.ayanlabs.core.data.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pms/api/tasks")
@Tag(name = "IPMS · TaskController", description = "Task management (Buildium-style CRUD)")
@RequiredArgsConstructor
public class TaskController extends IpmsCrudController<Task> {

    private static final String BASE_PATH = "/pms/api/tasks";
    private final ITaskService taskService;

    @Override
    protected ITaskService service() {
        return taskService;
    }

    @Override
    protected String basePath() {
        return BASE_PATH;
    }

    @Override
    protected String operationPrefix() {
        return "tasks";
    }

    @Override
    protected String entityLabel() {
        return "Task";
    }
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<Task>>> list(
            @ClientHeaders ClientHeadersPayload headers) {
        return executeList(headers, Map.of(), service()::findAll);
    }

}
