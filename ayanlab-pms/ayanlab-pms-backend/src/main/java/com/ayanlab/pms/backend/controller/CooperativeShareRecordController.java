package com.ayanlab.pms.backend.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.dto.ClientHeadersPayload;
import com.ayanlab.core.dto.response.ApiResponse;
import com.ayanlab.pms.backend.entity.associations.CooperativeShareRecord;
import com.ayanlab.pms.backend.service.ICooperativeShareRecordService;
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
@RequestMapping("/pms/api/cooperative-share-records")
@Tag(name = "IPMS · CooperativeShareRecordController", description = "Cooperative share record (P5.9)")
@RequiredArgsConstructor
public class CooperativeShareRecordController extends IpmsCrudController<CooperativeShareRecord> {

    private static final String BASE_PATH = "/pms/api/cooperative-share-records";
    private final ICooperativeShareRecordService cooperativeShareRecordService;

    @Override
    protected ICooperativeShareRecordService service() {
        return cooperativeShareRecordService;
    }

    @Override
    protected String basePath() {
        return BASE_PATH;
    }

    @Override
    protected String operationPrefix() {
        return "cooperative-share-records";
    }

    @Override
    protected String entityLabel() {
        return "CooperativeShareRecord";
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<CooperativeShareRecord>>> list(@ClientHeaders ClientHeadersPayload headers) {
        return executeList(headers, Map.of(), service()::findAll);
    }
}
