package com.ayanlabs.propertymgmt.controller;

import com.ayanlabs.core.annotation.ClientHeaders;
import com.ayanlabs.core.data.ClientHeadersPayload;
import com.ayanlabs.core.data.responses.ApiResponse;
import com.ayanlabs.propertymgmt.entity.reporting.AnalyticsSnapshot;
import com.ayanlabs.propertymgmt.service.IAnalyticsSnapshotService;
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
@RequestMapping("/pms/api/analytics-snapshots")
@Tag(name = "IPMS · AnalyticsSnapshotController", description = "AnalyticsSnapshot CRUD")
@RequiredArgsConstructor
public class AnalyticsSnapshotController extends IpmsCrudController<AnalyticsSnapshot> {

    private static final String BASE_PATH = "/pms/api/analytics-snapshots";
    private final IAnalyticsSnapshotService analyticsSnapshotService;

    @Override
    protected IAnalyticsSnapshotService service() {
        return analyticsSnapshotService;
    }

    @Override
    protected String basePath() {
        return BASE_PATH;
    }

    @Override
    protected String operationPrefix() {
        return "analytics-snapshots";
    }

    @Override
    protected String entityLabel() {
        return "AnalyticsSnapshot";
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<AnalyticsSnapshot>>> list(
            @ClientHeaders ClientHeadersPayload headers) {
        return executeList(headers, Map.of(), service()::findAll);
    }
}
