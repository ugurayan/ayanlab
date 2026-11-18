package com.ayanlab.pms.backend.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.data.ClientHeadersPayload;
import com.ayanlab.core.data.responses.ApiResponse;
import com.ayanlab.pms.backend.entity.addons.AddonCatalogItem;
import com.ayanlab.pms.backend.service.IAddonCatalogItemService;
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
@RequestMapping("/pms/api/addons")
@Tag(name = "IPMS · AddonCatalogItemController", description = "AddonCatalogItem CRUD")
@RequiredArgsConstructor
public class AddonCatalogItemController extends IpmsCrudController<AddonCatalogItem> {

    private static final String BASE_PATH = "/pms/api/addons";
    private final IAddonCatalogItemService addonCatalogItemService;

    @Override
    protected IAddonCatalogItemService service() {
        return addonCatalogItemService;
    }

    @Override
    protected String basePath() {
        return BASE_PATH;
    }

    @Override
    protected String operationPrefix() {
        return "addons";
    }

    @Override
    protected String entityLabel() {
        return "AddonCatalogItem";
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<AddonCatalogItem>>> list(
            @ClientHeaders ClientHeadersPayload headers) {
        return executeList(headers, Map.of(), service()::findAll);
    }
}
