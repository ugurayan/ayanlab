package com.ayanlab.pms.backend.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.dto.ClientHeadersPayload;
import com.ayanlab.core.dto.response.ApiResponse;
import com.ayanlab.pms.backend.entity.tr.PatrolCheckpoint;
import com.ayanlab.pms.backend.service.IPatrolCheckpointService;
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
@RequestMapping("/pms/api/patrol-checkpoints")
@Tag(name = "IPMS · PatrolCheckpointController", description = "Patrol checkpoint (TR module CRUD)")
@RequiredArgsConstructor
public class PatrolCheckpointController extends IpmsCrudController<PatrolCheckpoint> {

    private static final String BASE_PATH = "/pms/api/patrol-checkpoints";
    private final IPatrolCheckpointService patrolCheckpointService;

    @Override
    protected IPatrolCheckpointService service() {
        return patrolCheckpointService;
    }

    @Override
    protected String basePath() {
        return BASE_PATH;
    }

    @Override
    protected String operationPrefix() {
        return "patrol-checkpoints";
    }

    @Override
    protected String entityLabel() {
        return "PatrolCheckpoint";
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<PatrolCheckpoint>>> list(@ClientHeaders ClientHeadersPayload headers) {
        return executeList(headers, Map.of(), service()::findAll);
    }
}
