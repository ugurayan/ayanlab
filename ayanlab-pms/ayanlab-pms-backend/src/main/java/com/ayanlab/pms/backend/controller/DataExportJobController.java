package com.ayanlab.pms.backend.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.dto.ClientHeadersPayload;
import com.ayanlab.core.dto.response.ApiResponse;
import com.ayanlab.pms.backend.entity.settings.DataExportJob;
import com.ayanlab.pms.backend.service.IDataExportJobService;
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
@RequestMapping("/pms/api/data-exports")
@Tag(name = "IPMS · DataExportJobController", description = "DataExportJob CRUD")
@RequiredArgsConstructor
public class DataExportJobController extends IpmsCrudController<DataExportJob> {

    private static final String BASE_PATH = "/pms/api/data-exports";
    private final IDataExportJobService dataExportJobService;

    @Override
    protected IDataExportJobService service() {
        return dataExportJobService;
    }

    @Override
    protected String basePath() {
        return BASE_PATH;
    }

    @Override
    protected String operationPrefix() {
        return "data-exports";
    }

    @Override
    protected String entityLabel() {
        return "DataExportJob";
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<DataExportJob>>> list(
            @ClientHeaders ClientHeadersPayload headers) {
        return executeList(headers, Map.of(), service()::findAll);
    }
}
