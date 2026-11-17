package com.ayanlabs.core.controller;

import com.ayanlabs.core.condition.CoreBusinessApi;
import com.ayanlabs.core.data.ClientHeadersPayload;

import com.ayanlabs.core.annotation.ClientHeaders;

import com.ayanlabs.core.data.responses.ApiResponse;
import com.ayanlabs.core.entity.InvoiceLineItem;
import com.ayanlabs.core.service.IInvoiceLineItemService;
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
@CoreBusinessApi
@RestController
@RequestMapping("/core/api/invoice-line-items")
@Tag(name = "Core · InvoiceLineItemController", description = "Invoice line items")
@RequiredArgsConstructor
public class InvoiceLineItemController extends BaseController {

    private static final String BASE_PATH = "/core/api/invoice-line-items";

    private final IInvoiceLineItemService invoiceLineItemService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<InvoiceLineItem>> create(
            @Valid @RequestBody InvoiceLineItem lineItem,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        TracedCall call = tracedCall(log, "create-invoiceLineItem", "POST", BASE_PATH, headers, lineItem, null);
        try {
            InvoiceLineItem created = invoiceLineItemService.create(lineItem, call.context());
            ResponseEntity<ApiResponse<InvoiceLineItem>> response = ok(created, call.context());
            call.trace().success(Map.of("createdId", created != null ? created.getId() : null, "success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PutMapping(value = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<InvoiceLineItem>> update(
            @PathVariable UUID id,
            @Valid @RequestBody InvoiceLineItem lineItem,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("id", id);
        TracedCall call = tracedCall(log, "update-invoiceLineItem", "PUT", BASE_PATH + "/{id}", headers, lineItem, params);
        try {
            lineItem.setId(id);
            InvoiceLineItem updated = invoiceLineItemService.update(lineItem, call.context());
            ResponseEntity<ApiResponse<InvoiceLineItem>> response = ok(updated, call.context());
            call.trace().success(Map.of("updatedId", updated != null ? updated.getId() : null, "success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<InvoiceLineItem>> getById(
            @PathVariable UUID id,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("id", id);
        TracedCall call = tracedCall(log, "get-invoiceLineItem", "GET", BASE_PATH + "/{id}", headers, null, params);
        try {
            InvoiceLineItem found = invoiceLineItemService.findById(id, call.context());
            ResponseEntity<ApiResponse<InvoiceLineItem>> response = ok(found, call.context());
            call.trace().success(Map.of("foundId", found != null ? found.getId() : null, "success", found != null));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<InvoiceLineItem>>> listByInvoice(
            @RequestParam UUID invoiceId,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("invoiceId", invoiceId);
        TracedCall call = tracedCall(log, "list-invoiceLineItems", "GET", BASE_PATH, headers, null, params);
        try {
            List<InvoiceLineItem> rows = invoiceLineItemService.listByInvoice(invoiceId);
            ResponseEntity<ApiResponse<List<InvoiceLineItem>>> response = ok(rows, call.context());
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
            @RequestParam(name = "hard", defaultValue = "false") boolean hard,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("id", id);
        TracedCall call = tracedCall(log, "delete-invoiceLineItem", "DELETE", BASE_PATH + "/{id}", headers, null, params);
        try {
            if (hard) {
            invoiceLineItemService.hardDeleteById(id, call.context());
            } else {
            invoiceLineItemService.deleteById(id, call.context());
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
    public ResponseEntity<ApiResponse<InvoiceLineItem>> setActivation(
            @PathVariable UUID id,
            @RequestParam boolean active,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("id", id);
        params.put("active", active);
        TracedCall call = tracedCall(log, "set-activation", "PUT", BASE_PATH + "/{id}/activation", headers, null, params);
        try {
            InvoiceLineItem updated = invoiceLineItemService.setActive(id, active, call.context());
            ResponseEntity<ApiResponse<InvoiceLineItem>> response = ok(updated, call.context());
            call.trace().success(Map.of("id", id, "active", active, "success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

}
