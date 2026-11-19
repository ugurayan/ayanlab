package com.ayanlab.pms.backend.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.data.ClientHeadersPayload;
import com.ayanlab.core.data.responses.ApiResponse;
import com.ayanlab.pms.backend.entity.tr.MeterReading;
import com.ayanlab.pms.backend.service.IMeterReadingService;
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
@RequestMapping("/pms/api/meter-readings")
@Tag(name = "IPMS · MeterReadingController", description = "Meter reading (TR module CRUD)")
@RequiredArgsConstructor
public class MeterReadingController extends IpmsCrudController<MeterReading> {

    private static final String BASE_PATH = "/pms/api/meter-readings";
    private final IMeterReadingService meterReadingService;

    @Override
    protected IMeterReadingService service() {
        return meterReadingService;
    }

    @Override
    protected String basePath() {
        return BASE_PATH;
    }

    @Override
    protected String operationPrefix() {
        return "meter-readings";
    }

    @Override
    protected String entityLabel() {
        return "MeterReading";
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<MeterReading>>> list(@ClientHeaders ClientHeadersPayload headers) {
        return executeList(headers, Map.of(), service()::findAll);
    }
}
