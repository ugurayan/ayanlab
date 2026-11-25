package com.ayanlab.core.controller;

import com.ayanlab.core.condition.AuthPlatformApi;
import com.ayanlab.core.constant.ApiPathPrefixes;
import com.ayanlab.core.dto.ClientHeadersPayload;

import com.ayanlab.core.annotation.ClientHeaders;

import com.ayanlab.core.dto.response.ApiResponse;
import com.ayanlab.core.dto.response.PagedResponse;
import com.ayanlab.core.entity.Module;
import com.ayanlab.core.util.ApiCallLogger;
import com.ayanlab.core.service.IModuleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@AuthPlatformApi
@RestController
@RequestMapping(ApiPathPrefixes.AUTH + "/modules")
@Tag(name = "Auth · ModuleController", description = "Modules")
@RequiredArgsConstructor
public class ModuleController extends BaseController {

    private static final String BASE_PATH = ApiPathPrefixes.AUTH + "/modules";

    private final IModuleService moduleService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Module>> create(
            @RequestBody Module module,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        TracedCall call = tracedCall(log, "create-module", "POST", BASE_PATH, headers, module, null);
        try {
            Module created = moduleService.create(module, call.context());
            ResponseEntity<ApiResponse<Module>> response = ok(created, call.context());
            call.trace().success(Map.of("createdId", created != null ? created.getId() : null, "success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PutMapping(value = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Module>> update(
            @PathVariable UUID id,
            @RequestBody Module module,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("id", id);
        TracedCall call = tracedCall(log, "update-module", "PUT", BASE_PATH + "/{id}", headers, module, params);
        try {
            module.setId(id);
            Module updated = moduleService.update(module, call.context());
            ResponseEntity<ApiResponse<Module>> response = ok(updated, call.context());
            call.trace().success(Map.of("updatedId", updated != null ? updated.getId() : null, "success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<PagedResponse<Module>>> list(
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "20") int size,
            @RequestParam(value = "appId", required = false) UUID appId,
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "name", required = false) String name,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("page", page);
        params.put("size", size);
        params.put("appId", appId);
        params.put("code", code);
        params.put("name", name);
        TracedCall call = tracedCall(log, "list-modules", "GET", BASE_PATH, headers, null, params);
        try {
            PagedResponse<Module> paged = moduleService.findAll(page, size, appId, code, name, call.context());
            ResponseEntity<ApiResponse<PagedResponse<Module>>> response = ok(paged, call.context());
            call.trace().success(Map.of("count", paged.getContent() != null ? paged.getContent().size() : 0, "success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Module>> getById(
            @PathVariable UUID id,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("id", id);
        TracedCall call = tracedCall(log, "get-module", "GET", BASE_PATH + "/{id}", headers, null, params);
        try {
            Module found = moduleService.findById(id, call.context());
            ResponseEntity<ApiResponse<Module>> response = ok(found, call.context());
            call.trace().success(Map.of("foundId", found != null ? found.getId() : null, "success", found != null));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @DeleteMapping(value = "{id}")
    public ResponseEntity<ApiResponse<Object>> deleteById(
            @PathVariable UUID id,
            @RequestParam(name = "hard", defaultValue = "false") boolean hard,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("id", id);
        TracedCall call = tracedCall(log, "delete-module", "DELETE", BASE_PATH + "/{id}", headers, null, params);
        try {
            if (hard) {
            moduleService.hardDeleteById(id, call.context());
            } else {
            moduleService.deleteById(id, call.context());
            }
            ResponseEntity<ApiResponse<Object>> response = ok(null, call.context());
            call.trace().success(Map.of("deletedId", id, "success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PutMapping(value = "{id}/activation")
    public ResponseEntity<ApiResponse<Module>> setActivation(
            @PathVariable UUID id,
            @RequestParam boolean active,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("id", id);
        params.put("active", active);
        TracedCall call = tracedCall(log, "set-activation", "PUT", BASE_PATH + "/{id}/activation", headers, null, params);
        try {
            Module updated = moduleService.setActive(id, active, call.context());
            ResponseEntity<ApiResponse<Module>> response = ok(updated, call.context());
            call.trace().success(Map.of("id", id, "active", active, "success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

}
