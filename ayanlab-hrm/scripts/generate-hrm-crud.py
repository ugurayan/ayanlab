#!/usr/bin/env python3
"""Generate HRM CRUD boilerplate (entity, repo, service, controller).

Startup seed: MockDataSeeder + MockDataLoader on backend boot
(staffiq.db.reload-mock-data-on-startup=true) loads
classpath:db/staffiq-mock-data.json into JPA tables.
"""
from __future__ import annotations

import os
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1] / "ayanlab-hrm-backend/src/main/java/com/ayanlab/hrm/backend"

# (entity, table, apiPath, permResource, scopedList: profile|worker|org|verification|none)
SPECS = [
    ("HrmUser", "HrmUser", "users", "settings", "none"),
    ("Job", "Job", "jobs", "jobs", "none"),
    ("Task", "Task", "tasks", "tasks", "none"),
    ("Message", "Message", "messages", "inbox", "none"),
    ("Organization", "Organization", "organizations", "organizations", "none"),
    ("Location", "Location", "locations", "locations", "none"),
    ("Shift", "Shift", "shifts", "shifts", "none"),
    ("Application", "Application", "applications", "applications", "none"),
    ("Worker", "Worker", "workers", "workers", "none"),
    ("Document", "Document", "documents", "documents", "profile"),
    ("FinanceRecord", "FinanceRecord", "finance-records", "finance", "none"),
    ("TransactionRecord", "TransactionRecord", "transactions", "transactions", "none"),
    ("UserAudit", "UserAudit", "user-audits", "audit", "none"),
    ("SystemAudit", "SystemAudit", "system-audits", "audit", "none"),
    ("Setting", "Setting", "settings", "settings", "none"),
    ("Profile", "Profile", "profiles", "profile", "profile"),
    ("JobRole", "JobRole", "job-roles", "job_roles", "none"),
    ("JobRoleCategory", "JobRoleCategory", "job-role-categories", "industries", "none"),
    ("RoleType", "RoleType", "role-types", "job_roles", "none"),
    ("Skill", "Skill", "skills", "skills", "none"),
    ("Education", "Education", "educations", "education", "profile"),
    ("Certificate", "Certificate", "certificates", "certificates", "none"),
    ("WorkerSkill", "WorkerSkill", "worker-skills", "worker_skills", "profile"),
    ("WorkerCertificate", "WorkerCertificate", "worker-certificates", "worker_certificates", "profile"),
    ("WorkExperience", "WorkExperience", "work-experiences", "experiences", "profile"),
    ("ProfileLanguage", "ProfileLanguage", "profile-languages", "languages", "profile"),
    ("ProfileContact", "ProfileContact", "profile-contacts", "contacts", "profile"),
    ("ProfileLocationDetail", "ProfileLocationDetail", "profile-location-details", "profile_locations", "profile"),
    ("DocumentVerification", "DocumentVerification", "document-verifications", "verification", "verification"),
    ("Department", "Department", "departments", "departments", "none"),
    ("WorkerIncident", "WorkerIncident", "worker-incidents", "workers", "none"),
]

SKIP_ENTITY_FILES = {
    "Organization",
    "Location",
    "Shift",
    "Application",
    "Worker",
    "WorkerIncident",
}

ENTITY_TEMPLATE = """package com.ayanlab.hrm.backend.entity;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@Entity
@Table(name = "{table}")
public class {entity} implements ProfileLinkedEntity {{
    @Id
    private String id;

    private String profileId;
    private String orgId;
    private String workerId;
    private String platformUserId;
    private String platformRecordId;
    private String createdAt;
    private String updatedAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private JsonNode details;
}}
"""

REPO_TEMPLATE = """package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.{entity};
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface {entity}Repository extends JpaRepository<{entity}, String> {{
    List<{entity}> findByProfileId(String profileId);
    List<{entity}> findByOrgId(String orgId);
    List<{entity}> findByWorkerId(String workerId);
}}
"""

SERVICE_TEMPLATE = """package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.{entity};
import com.ayanlab.core.data.CoreContext;
import java.util.List;

public interface {entity}Service {{
    List<{entity}> findAll(CoreContext context);
    {entity} findById(String id, CoreContext context);
    {entity} create({entity} body, CoreContext context);
    {entity} update({entity} body, CoreContext context);
    void deleteById(String id, CoreContext context);
}}
"""

SERVICE_IMPL_TEMPLATE = """package com.ayanlab.hrm.backend.service.impl;

import com.ayanlab.hrm.backend.entity.{entity};
import com.ayanlab.hrm.backend.repository.{entity}Repository;
import com.ayanlab.hrm.backend.service.{entity}Service;
import com.ayanlab.hrm.backend.service.platform.HrmPlatformAuthorizationService;
import com.ayanlab.hrm.backend.service.support.HrmProfileContextSupport;
import com.ayanlab.core.data.CoreContext;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
{extra_imports}

@Service
@RequiredArgsConstructor
public class {entity}ServiceImpl implements {entity}Service {{

    private static final String PERM_READ = "staffiq:{perm}:read";
    private static final String PERM_CREATE = "staffiq:{perm}:create";
    private static final String PERM_UPDATE = "staffiq:{perm}:update";
    private static final String PERM_DELETE = "staffiq:{perm}:delete";

    private final {entity}Repository repository;
    private final HrmPlatformAuthorizationService platformAuthorization;
{extra_fields}

    @Override
    public List<{entity}> findAll(CoreContext context) {{
        platformAuthorization.requirePermission(context, PERM_READ);
{find_all_body}
    }}

    @Override
    public {entity} findById(String id, CoreContext context) {{
        platformAuthorization.requirePermission(context, PERM_READ);
        {entity} row = repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "{entity} not found"));
        HrmProfileContextSupport.assertReadable(row, context);
        return row;
    }}

    @Override
    @Transactional
    public {entity} create({entity} body, CoreContext context) {{
        platformAuthorization.requirePermission(context, PERM_CREATE);
        if (body.getId() == null || body.getId().isBlank()) {{
            body.setId(UUID.randomUUID().toString());
        }}
        HrmProfileContextSupport.stampFromContext(body, context);
        return repository.save(body);
    }}

    @Override
    @Transactional
    public {entity} update({entity} body, CoreContext context) {{
        platformAuthorization.requirePermission(context, PERM_UPDATE);
        {entity} existing = repository
                .findById(body.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "{entity} not found"));
        HrmProfileContextSupport.assertReadable(existing, context);
        HrmProfileContextSupport.stampFromContext(body, context);
        return repository.save(body);
    }}

    @Override
    @Transactional
    public void deleteById(String id, CoreContext context) {{
        platformAuthorization.requirePermission(context, PERM_DELETE);
        {entity} existing = repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "{entity} not found"));
        HrmProfileContextSupport.assertReadable(existing, context);
        repository.deleteById(id);
    }}
}}
"""

SERVICE_IMPL_DOMAIN_TEMPLATE = """package com.ayanlab.hrm.backend.service.impl;

import com.ayanlab.hrm.backend.entity.{entity};
import com.ayanlab.hrm.backend.repository.{entity}Repository;
import com.ayanlab.hrm.backend.service.{entity}Service;
import com.ayanlab.hrm.backend.service.platform.HrmPlatformAuthorizationService;
import com.ayanlab.core.data.CoreContext;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
{extra_imports}

@Service
@RequiredArgsConstructor
public class {entity}ServiceImpl implements {entity}Service {{

    private static final String PERM_READ = "staffiq:{perm}:read";
    private static final String PERM_CREATE = "staffiq:{perm}:create";
    private static final String PERM_UPDATE = "staffiq:{perm}:update";
    private static final String PERM_DELETE = "staffiq:{perm}:delete";

    private final {entity}Repository repository;
    private final HrmPlatformAuthorizationService platformAuthorization;
{extra_fields}

    @Override
    public List<{entity}> findAll(CoreContext context) {{
        platformAuthorization.requirePermission(context, PERM_READ);
{find_all_body}
    }}

    @Override
    public {entity} findById(String id, CoreContext context) {{
        platformAuthorization.requirePermission(context, PERM_READ);
        return repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "{entity} not found"));
    }}

    @Override
    @Transactional
    public {entity} create({entity} body, CoreContext context) {{
        platformAuthorization.requirePermission(context, PERM_CREATE);
        if (body.getId() == null || body.getId().isBlank()) {{
            body.setId(UUID.randomUUID().toString());
        }}
        return repository.save(body);
    }}

    @Override
    @Transactional
    public {entity} update({entity} body, CoreContext context) {{
        platformAuthorization.requirePermission(context, PERM_UPDATE);
        if (!repository.existsById(body.getId())) {{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "{entity} not found");
        }}
        return repository.save(body);
    }}

    @Override
    @Transactional
    public void deleteById(String id, CoreContext context) {{
        platformAuthorization.requirePermission(context, PERM_DELETE);
        if (!repository.existsById(id)) {{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "{entity} not found");
        }}
        repository.deleteById(id);
    }}
}}
"""

CONTROLLER_TEMPLATE = """package com.ayanlab.hrm.backend.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.annotation.RequirePermission;
import com.ayanlab.core.controller.BaseController;
import com.ayanlab.core.data.ClientHeadersPayload;
import com.ayanlab.core.data.responses.ApiResponse;
import com.ayanlab.hrm.backend.entity.{entity};
import com.ayanlab.hrm.backend.service.{entity}Service;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/hrm/api/{path}")
@RequiredArgsConstructor
@Tag(name = "HRM · {entity}", description = "{entity} CRUD")
public class {entity}Controller extends BaseController {{

    private static final String BASE = "/hrm/api/{path}";
    private final {entity}Service service;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:{perm}:read")
    public ResponseEntity<ApiResponse<List<{entity}>>> list(@ClientHeaders ClientHeadersPayload headers) {{
        TracedCall call = tracedCall(log, "{op}-list", "GET", BASE, headers, null, null);
        try {{
            List<{entity}> data = service.findAll(call.context());
            call.trace().success(Map.of("count", data.size()));
            return ok(data, call.context());
        }} catch (Exception e) {{
            call.trace().error(e);
            throw e;
        }}
    }}

    @GetMapping(value = "/{{id}}", produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:{perm}:read")
    public ResponseEntity<ApiResponse<{entity}>> get(
            @PathVariable String id, @ClientHeaders ClientHeadersPayload headers) {{
        TracedCall call = tracedCall(log, "{op}-get", "GET", BASE + "/{{id}}", headers, null, Map.of("id", id));
        try {{
            {entity} data = service.findById(id, call.context());
            call.trace().success(Map.of("id", id));
            return ok(data, call.context());
        }} catch (Exception e) {{
            call.trace().error(e);
            throw e;
        }}
    }}

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:{perm}:create")
    public ResponseEntity<ApiResponse<{entity}>> create(
            @RequestBody {entity} body, @ClientHeaders ClientHeadersPayload headers) {{
        TracedCall call = tracedCall(log, "{op}-create", "POST", BASE, headers, body, null);
        try {{
            {entity} created = service.create(body, call.context());
            call.trace().success(Map.of("id", created.getId()));
            return createdBody(created, "{entity} created", call.context());
        }} catch (Exception e) {{
            call.trace().error(e);
            throw e;
        }}
    }}

    @PutMapping(value = "/{{id}}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:{perm}:update")
    public ResponseEntity<ApiResponse<{entity}>> update(
            @PathVariable String id, @RequestBody {entity} body, @ClientHeaders ClientHeadersPayload headers) {{
        body.setId(id);
        TracedCall call = tracedCall(log, "{op}-update", "PUT", BASE + "/{{id}}", headers, body, Map.of("id", id));
        try {{
            {entity} updated = service.update(body, call.context());
            call.trace().success(Map.of("id", id));
            return ok(updated, call.context());
        }} catch (Exception e) {{
            call.trace().error(e);
            throw e;
        }}
    }}

    @DeleteMapping("/{{id}}")
    @RequirePermission("staffiq:{perm}:delete")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable String id, @ClientHeaders ClientHeadersPayload headers) {{
        TracedCall call = tracedCall(log, "{op}-delete", "DELETE", BASE + "/{{id}}", headers, null, Map.of("id", id));
        try {{
            service.deleteById(id, call.context());
            call.trace().success(Map.of("id", id));
            return ok(null, call.context());
        }} catch (Exception e) {{
            call.trace().error(e);
            throw e;
        }}
    }}
}}
"""

CRUD_SERVICE_TEMPLATE = """package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.{entity};
import com.ayanlab.core.data.CoreContext;
import java.util.List;

public interface {entity}Service {{
    List<{entity}> findAll(CoreContext context);
    {entity} findById(String id, CoreContext context);
    {entity} create({entity} body, CoreContext context);
    {entity} update({entity} body, CoreContext context);
    void deleteById(String id, CoreContext context);
}}
"""

def scoped_find_all(scoped: str) -> str:
    if scoped == "profile":
        return """        if (context.getProfileId() != null) {
            return repository.findByProfileId(context.getProfileId().toString());
        }
        if (context.getOrgId() != null) {
            return repository.findByOrgId(context.getOrgId().toString());
        }
        return repository.findAll();"""
    if scoped == "verification":
        return """        if (context.getOrgId() != null) {
            return repository.findByOrgId(context.getOrgId().toString());
        }
        if (context.getProfileId() != null) {
            return repository.findByWorkerId(context.getProfileId().toString());
        }
        return repository.findAll();"""
    if scoped == "org":
        return """        if (context.getOrgId() != null) {
            return repository.findByOrgId(context.getOrgId().toString());
        }
        return repository.findAll();"""
    return "        return repository.findAll();"


def write(path: Path, content: str) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    if path.exists() and path.name in {
        "Organization.java",
        "Location.java",
        "Shift.java",
        "Application.java",
        "Worker.java",
        "WorkerIncident.java",
        "OrganizationRepository.java",
        "LocationRepository.java",
        "ShiftRepository.java",
        "ApplicationRepository.java",
        "WorkerRepository.java",
        "WorkerIncidentRepository.java",
    }:
        return
    path.write_text(content, encoding="utf-8")


def main() -> None:
    for entity, table, api_path, perm, scoped in SPECS:
        op = api_path.replace("-", "_")

        if entity not in SKIP_ENTITY_FILES:
            write(ROOT / "entity" / f"{entity}.java", ENTITY_TEMPLATE.format(entity=entity, table=table))

        write(ROOT / "repository" / f"{entity}Repository.java", REPO_TEMPLATE.format(entity=entity))

        write(
            ROOT / "service" / f"{entity}Service.java",
            SERVICE_TEMPLATE.format(entity=entity),
        )

        impl = SERVICE_IMPL_TEMPLATE.format(
            entity=entity,
            perm=perm,
            extra_imports="",
            extra_fields="",
            find_all_body=scoped_find_all(scoped),
        )
        write(ROOT / "service" / "impl" / f"{entity}ServiceImpl.java", impl)

        write(
            ROOT / "controller" / f"{entity}Controller.java",
            CONTROLLER_TEMPLATE.format(entity=entity, path=api_path, perm=perm, op=op),
        )

    # Crud services for existing domain entities (no new entity file)
    for entity, table, api_path, perm, scoped in SPECS:
        if entity not in SKIP_ENTITY_FILES:
            continue
        write(
            ROOT / "service" / f"{entity}Service.java",
            CRUD_SERVICE_TEMPLATE.format(entity=entity),
        )
        impl = SERVICE_IMPL_DOMAIN_TEMPLATE.format(
            entity=entity,
            perm=perm,
            extra_imports="",
            extra_fields="",
            find_all_body="        return repository.findAll();",
        )
        write(ROOT / "service" / "impl" / f"{entity}ServiceImpl.java", impl)
        write(
            ROOT / "controller" / f"{entity}Controller.java",
            CONTROLLER_TEMPLATE.format(entity=entity, path=api_path, perm=perm, op=api_path.replace("-", "_")),
        )

    print(f"Generated CRUD under {ROOT}")


if __name__ == "__main__":
    main()