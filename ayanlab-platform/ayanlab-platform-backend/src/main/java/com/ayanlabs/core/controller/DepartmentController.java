package com.ayanlabs.core.controller;

import com.ayanlabs.core.annotation.ClientHeaders;
import com.ayanlabs.core.condition.AuthPlatformApi;
import com.ayanlabs.core.data.ClientHeadersPayload;
import com.ayanlabs.core.data.responses.ApiResponse;
import com.ayanlabs.core.entity.Department;
import com.ayanlabs.core.service.IDepartmentService;
import com.ayanlabs.core.web.ApiPathPrefixes;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@AuthPlatformApi
@RestController
@RequestMapping(ApiPathPrefixes.AUTH + "/departments")
@Tag(name = "Auth · Department", description = "Organization departments (tmp/auth)")
@RequiredArgsConstructor
public class DepartmentController extends BaseController {

    private static final String BASE = ApiPathPrefixes.AUTH + "/departments";
    private final IDepartmentService departmentService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Department>> create(@RequestBody Department body, @ClientHeaders ClientHeadersPayload headers) {
        var call = tracedCall(log, "create-department", "POST", BASE, headers, body, null);
        Department created = departmentService.create(body, call.context());
        call.trace().success(Map.of("id", created.getId()));
        return ok(created, call.context());
    }

    @PutMapping(value = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Department>> update(@PathVariable UUID id, @RequestBody Department body, @ClientHeaders ClientHeadersPayload headers) {
        body.setId(id);
        var call = tracedCall(log, "update-department", "PUT", BASE + "/{id}", headers, body, Map.of("id", id));
        return ok(departmentService.update(body, call.context()), call.context());
    }

    @GetMapping(value = "organization/{organizationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<Department>>> byOrg(@PathVariable UUID organizationId, @ClientHeaders ClientHeadersPayload headers) {
        var call = tracedCall(log, "list-departments", "GET", BASE, headers, null, Map.of("organizationId", organizationId));
        List<Department> data = departmentService.findByOrganizationId(organizationId, call.context());
        return ok(data, call.context());
    }

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Department>> get(@PathVariable UUID id, @ClientHeaders ClientHeadersPayload headers) {
        var call = tracedCall(log, "get-department", "GET", BASE + "/{id}", headers, null, Map.of("id", id));
        return ok(departmentService.findById(id, call.context()), call.context());
    }

    @DeleteMapping(value = "{id}")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable UUID id,
            @RequestParam(name = "hard", defaultValue = "false") boolean hard,
            @ClientHeaders ClientHeadersPayload headers) {
        var call = tracedCall(log, "delete-department", "DELETE", BASE + "/{id}", headers, null, Map.of("id", id));
        if (hard) {
            departmentService.hardDeleteById(id, call.context());
            } else {
            departmentService.deleteById(id, call.context());
            }
        return ok(null, call.context());
    }
}