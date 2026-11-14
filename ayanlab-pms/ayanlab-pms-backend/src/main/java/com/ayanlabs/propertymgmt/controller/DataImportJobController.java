package com.ayanlabs.propertymgmt.controller;

import com.ayanlabs.core.annotation.ClientHeaders;
import com.ayanlabs.core.data.ClientHeadersPayload;
import com.ayanlabs.core.data.responses.ApiResponse;
import com.ayanlabs.propertymgmt.entity.settings.DataImportJob;
import com.ayanlabs.propertymgmt.service.IDataImportJobService;
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
@RequestMapping("/ayanlabs/api/property-mgmt/data-imports")
@Tag(name = "IPMS · DataImportJobController", description = "DataImportJob CRUD")
@RequiredArgsConstructor
public class DataImportJobController extends IpmsCrudController<DataImportJob> {

    private static final String BASE_PATH = "/ayanlabs/api/property-mgmt/data-imports";
    private final IDataImportJobService dataImportJobService;

    @Override
    protected IDataImportJobService service() {
        return dataImportJobService;
    }

    @Override
    protected String basePath() {
        return BASE_PATH;
    }

    @Override
    protected String operationPrefix() {
        return "data-imports";
    }

    @Override
    protected String entityLabel() {
        return "DataImportJob";
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<DataImportJob>>> list(
            @ClientHeaders ClientHeadersPayload headers) {
        return executeList(headers, Map.of(), service()::findAll);
    }
}
