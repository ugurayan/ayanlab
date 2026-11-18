package com.ayanlab.pms.backend.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.data.ClientHeadersPayload;
import com.ayanlab.core.data.responses.ApiResponse;
import com.ayanlab.pms.backend.entity.settings.ResidentCenterContact;
import com.ayanlab.pms.backend.service.IResidentCenterContactService;
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
@RequestMapping("/pms/api/resident-center-contacts")
@Tag(name = "IPMS · ResidentCenterContactController", description = "ResidentCenterContact CRUD")
@RequiredArgsConstructor
public class ResidentCenterContactController extends IpmsCrudController<ResidentCenterContact> {

    private static final String BASE_PATH = "/pms/api/resident-center-contacts";
    private final IResidentCenterContactService residentCenterContactService;

    @Override
    protected IResidentCenterContactService service() {
        return residentCenterContactService;
    }

    @Override
    protected String basePath() {
        return BASE_PATH;
    }

    @Override
    protected String operationPrefix() {
        return "resident-center-contacts";
    }

    @Override
    protected String entityLabel() {
        return "ResidentCenterContact";
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<ResidentCenterContact>>> list(
            @ClientHeaders ClientHeadersPayload headers) {
        return executeList(headers, Map.of(), service()::findAll);
    }
}
