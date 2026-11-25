package com.ayanlab.pms.backend.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.controller.BaseController;
import com.ayanlab.core.dto.ClientHeadersPayload;
import com.ayanlab.core.dto.CoreContext;
import com.ayanlab.core.dto.response.ApiResponse;
import com.ayanlab.core.entity.BaseEntity;
import com.ayanlab.pms.backend.security.IpmsAuthz;
import com.ayanlab.pms.backend.service.IIpmsOrgScopedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public abstract class IpmsCrudController<T extends BaseEntity> extends BaseController {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected abstract IIpmsOrgScopedService<T> service();

    protected abstract String basePath();

    protected abstract String operationPrefix();

    protected abstract String entityLabel();

    protected ResponseEntity<ApiResponse<List<T>>> executeList(
            ClientHeadersPayload headers,
            Map<String, Object> traceParams,
            Function<CoreContext, List<T>> loader) {
        IpmsAuthz.requireOperation(operationPrefix(), "read");
        TracedCall call = tracedCall(log, operationPrefix() + "-list", "GET", basePath(), headers, null, traceParams);
        try {
            List<T> data = loader.apply(call.context());
            call.trace().success(Map.of("count", data.size(), "success", true));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<T>> getById(
            @PathVariable UUID id,
            @ClientHeaders ClientHeadersPayload headers) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("id", id);
        IpmsAuthz.requireOperation(operationPrefix(), "read");
        TracedCall call = tracedCall(log, operationPrefix() + "-get", "GET", basePath() + "/{id}", headers, null, params);
        try {
            T found = service().findById(id, call.context());
            call.trace().success(Map.of("foundId", id, "success", true));
            return ok(found, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<T>> create(
            @RequestBody T body,
            @ClientHeaders ClientHeadersPayload headers) {
        IpmsAuthz.requireOperation(operationPrefix(), "create");
        TracedCall call = tracedCall(log, operationPrefix() + "-create", "POST", basePath(), headers, body, null);
        try {
            T created = service().create(body, call.context());
            call.trace().success(Map.of("createdId", created.getId(), "success", true));
            return createdBody(created, entityLabel() + " created", call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<T>> update(
            @PathVariable UUID id,
            @RequestBody T body,
            @ClientHeaders ClientHeadersPayload headers) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("id", id);
        body.setId(id);
        IpmsAuthz.requireOperation(operationPrefix(), "update");
        TracedCall call = tracedCall(log, operationPrefix() + "-update", "PUT", basePath() + "/{id}", headers, body, params);
        try {
            T updated = service().update(body, call.context());
            call.trace().success(Map.of("updatedId", updated.getId(), "success", true));
            return ok(updated, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteById(
            @PathVariable UUID id,
            @ClientHeaders ClientHeadersPayload headers) {
        IpmsAuthz.requireOperation(operationPrefix(), "delete");
        TracedCall call = tracedCall(log, operationPrefix() + "-delete", "DELETE", basePath() + "/{id}", headers, null, Map.of("id", id));
        try {
            service().deleteById(id, call.context());
            call.trace().success(Map.of("deletedId", id, "success", true));
            return ok(null, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }
}
