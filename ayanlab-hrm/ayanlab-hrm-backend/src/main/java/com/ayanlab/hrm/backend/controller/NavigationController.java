package com.ayanlab.hrm.backend.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.annotation.RequirePermission;
import com.ayanlab.core.controller.BaseController;
import com.ayanlab.core.dto.ClientHeadersPayload;
import com.ayanlab.core.dto.response.ApiResponse;
import com.ayanlab.core.provider.AppSecurityContext;
import com.ayanlab.hrm.backend.dto.response.HrmShellsResponse;
import com.ayanlab.hrm.backend.entity.Locale;
import com.ayanlab.hrm.backend.service.NavigationService;
import com.ayanlab.hrm.backend.util.HrmShells;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/hrm/api/navigation")
@RequiredArgsConstructor
@Tag(name = "StaffIQ · Navigation", description = "Sidebar menu items and i18n labels")
public class NavigationController extends BaseController {

    private static final String BASE_PATH = "/hrm/api/navigation";

    private final NavigationService navigationService;

    @GetMapping("/menu-items")
    @RequirePermission("staffiq:dashboard:read")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> listMenuItems(
            @RequestParam(name = "locale", defaultValue = "en") String locale,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call =
                tracedCall(log, "list-menu-items", "GET", BASE_PATH + "/menu-items", headers, null, Map.of("locale", locale));
        try {
            List<Map<String, Object>> data = navigationService.listMenuItemsForLocale(locale);
            ResponseEntity<ApiResponse<List<Map<String, Object>>>> response = okBody(data, call.context());
            call.trace().success(Map.of("count", data.size()));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping("/shells")
    @RequirePermission("staffiq:dashboard:read")
    public ResponseEntity<ApiResponse<HrmShellsResponse>> listShells(@ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "list-shells", "GET", BASE_PATH + "/shells", headers, null, null);
        try {
            HrmShellsResponse data = new HrmShellsResponse(HrmShells.available(AppSecurityContext.get()));
            ResponseEntity<ApiResponse<HrmShellsResponse>> response = okBody(data, call.context());
            call.trace().success(Map.of("count", data.shells().size()));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping("/locales")
    @RequirePermission("staffiq:dashboard:read")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> listLocales(
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "list-locales", "GET", BASE_PATH + "/locales", headers, null, null);
        try {
            List<Map<String, Object>> data =
                    navigationService.listEnabledLocales().stream().map(this::localeRow).toList();
            ResponseEntity<ApiResponse<List<Map<String, Object>>>> response = okBody(data, call.context());
            call.trace().success(Map.of("count", data.size()));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    private Map<String, Object> localeRow(Locale locale) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("code", locale.getCode());
        row.put("displayName", locale.getDisplayName());
        row.put("bcp47Tag", locale.getBcp47Tag());
        row.put("sortOrder", locale.getSortOrder());
        return row;
    }
}