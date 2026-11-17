package com.ayanlabs.propertymgmt.controller;

import com.ayanlabs.core.annotation.ClientHeaders;
import com.ayanlabs.core.data.ClientHeadersPayload;
import com.ayanlabs.core.data.responses.ApiResponse;
import com.ayanlabs.propertymgmt.entity.settings.PublicSiteSetting;
import com.ayanlabs.propertymgmt.service.IPublicSiteSettingService;
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
@RequestMapping("/pms/api/public-site-settings")
@Tag(name = "IPMS · PublicSiteSettingController", description = "PublicSiteSetting CRUD")
@RequiredArgsConstructor
public class PublicSiteSettingController extends IpmsCrudController<PublicSiteSetting> {

    private static final String BASE_PATH = "/pms/api/public-site-settings";
    private final IPublicSiteSettingService publicSiteSettingService;

    @Override
    protected IPublicSiteSettingService service() {
        return publicSiteSettingService;
    }

    @Override
    protected String basePath() {
        return BASE_PATH;
    }

    @Override
    protected String operationPrefix() {
        return "public-site-settings";
    }

    @Override
    protected String entityLabel() {
        return "PublicSiteSetting";
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<PublicSiteSetting>>> list(
            @ClientHeaders ClientHeadersPayload headers) {
        return executeList(headers, Map.of(), service()::findAll);
    }
}
