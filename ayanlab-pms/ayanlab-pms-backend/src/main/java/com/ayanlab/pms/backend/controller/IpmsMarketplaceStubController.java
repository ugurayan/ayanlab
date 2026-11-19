package com.ayanlab.pms.backend.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.annotation.RequirePermission;
import com.ayanlab.core.controller.BaseController;
import com.ayanlab.core.data.ClientHeadersPayload;
import com.ayanlab.core.data.responses.ApiResponse;
import com.ayanlab.pms.backend.entity.addons.AddonCatalogItem;
import com.ayanlab.pms.backend.service.IAddonCatalogItemService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pms/api/marketplace")
@Tag(name = "IPMS · Marketplace", description = "Add-on catalog + platform billing stub (B0.6)")
@Slf4j
@RequiredArgsConstructor
public class IpmsMarketplaceStubController extends BaseController {

    private final IAddonCatalogItemService addonCatalogItemService;

    @Value("${platform.api-url:http://localhost:8102}")
    private String platformApiUrl;

    @GetMapping(value = "/billing-bridge", produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("ipms:addons:read")
    public ResponseEntity<ApiResponse<Map<String, Object>>> billingBridge(
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "marketplace-billing-bridge", "GET", "/pms/api/marketplace/billing-bridge", headers, null, null);
        List<AddonCatalogItem> addons = addonCatalogItemService.findAll(call.context());
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("platformBaseUrl", platformApiUrl);
        data.put("invoicesPath", "/core/api/invoices");
        data.put("subscriptionAccountsPath", "/pms/api/subscription-accounts");
        data.put("addonCount", addons.size());
        data.put("status", "STUB_LINKED");
        return ok(data, call.context());
    }
}