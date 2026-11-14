package com.ayanlabs.propertymgmt.controller;

import com.ayanlabs.core.annotation.ClientHeaders;
import com.ayanlabs.core.data.ClientHeadersPayload;
import com.ayanlabs.core.data.responses.ApiResponse;
import com.ayanlabs.propertymgmt.entity.settings.OrgSetting;
import com.ayanlabs.propertymgmt.service.IOrgSettingService;
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
@RequestMapping("/ayanlabs/api/property-mgmt/org-settings")
@Tag(name = "IPMS · OrgSettingController", description = "OrgSetting CRUD")
@RequiredArgsConstructor
public class OrgSettingController extends IpmsCrudController<OrgSetting> {

    private static final String BASE_PATH = "/ayanlabs/api/property-mgmt/org-settings";
    private final IOrgSettingService orgSettingService;

    @Override
    protected IOrgSettingService service() {
        return orgSettingService;
    }

    @Override
    protected String basePath() {
        return BASE_PATH;
    }

    @Override
    protected String operationPrefix() {
        return "org-settings";
    }

    @Override
    protected String entityLabel() {
        return "OrgSetting";
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<OrgSetting>>> list(
            @ClientHeaders ClientHeadersPayload headers) {
        return executeList(headers, Map.of(), service()::findAll);
    }
}
