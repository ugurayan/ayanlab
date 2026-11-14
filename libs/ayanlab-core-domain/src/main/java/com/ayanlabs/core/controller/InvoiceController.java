package com.ayanlabs.core.controller;

import com.ayanlabs.core.data.ClientHeadersPayload;

import com.ayanlabs.core.annotation.ClientHeaders;

import com.ayanlabs.core.data.enums.InvoiceStatusEnum;
import com.ayanlabs.core.data.responses.ApiResponse;
import com.ayanlabs.core.entity.Invoice;
import com.ayanlabs.core.service.IInvoiceService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/core/invoices")
@Tag(name = "Core · InvoiceController", description = "Billing invoices")
@RequiredArgsConstructor
public class InvoiceController extends BaseController {

    private static final String BASE_PATH = "/api/core/invoices";

    private final IInvoiceService invoiceService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Invoice>> create(
            @Valid @RequestBody Invoice invoice,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        TracedCall call = tracedCall(log, "create-invoice", "POST", BASE_PATH, headers, invoice, null);
        try {
            Invoice created = invoiceService.create(invoice, call.context());
            ResponseEntity<ApiResponse<Invoice>> response = ok(created, call.context());
            call.trace().success(Map.of("createdId", created != null ? created.getId() : null, "success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PutMapping(value = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Invoice>> update(
            @PathVariable UUID id,
            @Valid @RequestBody Invoice invoice,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("id", id);
        TracedCall call = tracedCall(log, "update-invoice", "PUT", BASE_PATH + "/{id}", headers, invoice, params);
        try {
            invoice.setId(id);
            Invoice updated = invoiceService.update(invoice, call.context());
            ResponseEntity<ApiResponse<Invoice>> response = ok(updated, call.context());
            call.trace().success(Map.of("updatedId", updated != null ? updated.getId() : null, "success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Invoice>> getById(
            @PathVariable UUID id,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("id", id);
        TracedCall call = tracedCall(log, "get-invoice", "GET", BASE_PATH + "/{id}", headers, null, params);
        try {
            Invoice found = invoiceService.findById(id, call.context());
            ResponseEntity<ApiResponse<Invoice>> response = ok(found, call.context());
            call.trace().success(Map.of("foundId", found != null ? found.getId() : null, "success", found != null));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<Invoice>>> list(
            @RequestParam UUID appId,
            @RequestParam(required = false) InvoiceStatusEnum status,
            @RequestParam(required = false) UUID profileId,
            @RequestParam(required = false) UUID organizationId,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("appId", appId);
        params.put("status", status);
        params.put("profileId", profileId);
        params.put("organizationId", organizationId);
        TracedCall call = tracedCall(log, "list-invoices", "GET", BASE_PATH, headers, null, params);
        try {
            List<Invoice> rows;
            if (profileId != null) {
                rows = invoiceService.listByProfile(profileId);
            } else if (organizationId != null) {
                rows = invoiceService.listByOrganization(organizationId);
            } else {
                rows = invoiceService.listByApp(appId, status);
            }
            ResponseEntity<ApiResponse<List<Invoice>>> response = ok(rows, call.context());
            call.trace().success(Map.of("count", rows.size(), "success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @DeleteMapping(value = "{id}")
    public ResponseEntity<ApiResponse<Object>> deleteById(
            @PathVariable UUID id,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("id", id);
        TracedCall call = tracedCall(log, "delete-invoice", "DELETE", BASE_PATH + "/{id}", headers, null, params);
        try {
            invoiceService.deleteById(id, call.context());
            ResponseEntity<ApiResponse<Object>> response = ok(null, call.context());
            call.trace().success(Map.of("deletedId", id, "success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }
}
