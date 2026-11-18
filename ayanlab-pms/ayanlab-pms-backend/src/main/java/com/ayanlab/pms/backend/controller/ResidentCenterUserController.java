package com.ayanlab.pms.backend.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.data.ClientHeadersPayload;
import com.ayanlab.core.data.responses.ApiResponse;
import com.ayanlab.pms.backend.entity.settings.ResidentCenterUser;
import com.ayanlab.pms.backend.service.IResidentCenterUserService;
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
@RequestMapping("/pms/api/resident-center-users")
@Tag(name = "IPMS · ResidentCenterUserController", description = "ResidentCenterUser CRUD")
@RequiredArgsConstructor
public class ResidentCenterUserController extends IpmsCrudController<ResidentCenterUser> {

    private static final String BASE_PATH = "/pms/api/resident-center-users";
    private final IResidentCenterUserService residentCenterUserService;

    @Override
    protected IResidentCenterUserService service() {
        return residentCenterUserService;
    }

    @Override
    protected String basePath() {
        return BASE_PATH;
    }

    @Override
    protected String operationPrefix() {
        return "resident-center-users";
    }

    @Override
    protected String entityLabel() {
        return "ResidentCenterUser";
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<ResidentCenterUser>>> list(
            @ClientHeaders ClientHeadersPayload headers) {
        return executeList(headers, Map.of(), service()::findAll);
    }
}
