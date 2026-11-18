package com.ayanlab.core.controller;

import com.ayanlab.core.condition.CoreBusinessApi;
import com.ayanlab.core.data.ClientHeadersPayload;

import com.ayanlab.core.annotation.ClientHeaders;

import com.ayanlab.core.data.enums.PaymentStatusEnum;
import com.ayanlab.core.data.responses.ApiResponse;
import com.ayanlab.core.entity.Payment;
import com.ayanlab.core.service.IPaymentService;
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
@RequestMapping("/core/api/payments")
@Tag(name = "Core · PaymentController", description = "Payments applied to invoices / subscriptions")
@RequiredArgsConstructor
public class PaymentController extends BaseController {

    private static final String BASE_PATH = "/core/api/payments";

    private final IPaymentService paymentService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Payment>> create(
            @Valid @RequestBody Payment payment,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        TracedCall call = tracedCall(log, "create-payment", "POST", BASE_PATH, headers, payment, null);
        try {
            Payment created = paymentService.create(payment, call.context());
            ResponseEntity<ApiResponse<Payment>> response = ok(created, call.context());
            call.trace().success(Map.of("createdId", created != null ? created.getId() : null, "success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PutMapping(value = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Payment>> update(
            @PathVariable UUID id,
            @Valid @RequestBody Payment payment,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("id", id);
        TracedCall call = tracedCall(log, "update-payment", "PUT", BASE_PATH + "/{id}", headers, payment, params);
        try {
            payment.setId(id);
            Payment updated = paymentService.update(payment, call.context());
            ResponseEntity<ApiResponse<Payment>> response = ok(updated, call.context());
            call.trace().success(Map.of("updatedId", updated != null ? updated.getId() : null, "success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Payment>> getById(
            @PathVariable UUID id,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("id", id);
        TracedCall call = tracedCall(log, "get-payment", "GET", BASE_PATH + "/{id}", headers, null, params);
        try {
            Payment found = paymentService.findById(id, call.context());
            ResponseEntity<ApiResponse<Payment>> response = ok(found, call.context());
            call.trace().success(Map.of("foundId", found != null ? found.getId() : null, "success", found != null));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<Payment>>> list(
            @RequestParam UUID appId,
            @RequestParam(required = false) PaymentStatusEnum status,
            @RequestParam(required = false) UUID invoiceId,
            @RequestParam(required = false) UUID transactionId,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("appId", appId);
        params.put("status", status);
        params.put("invoiceId", invoiceId);
        params.put("transactionId", transactionId);
        TracedCall call = tracedCall(log, "list-payments", "GET", BASE_PATH, headers, null, params);
        try {
            List<Payment> rows;
            if (invoiceId != null) {
                rows = paymentService.listByInvoice(invoiceId);
            } else if (transactionId != null) {
                rows = paymentService.findByTransactionId(transactionId).map(List::of).orElse(List.of());
            } else {
                rows = paymentService.listByApp(appId, status);
            }
            ResponseEntity<ApiResponse<List<Payment>>> response = ok(rows, call.context());
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
        TracedCall call = tracedCall(log, "delete-payment", "DELETE", BASE_PATH + "/{id}", headers, null, params);
        try {
            if (hard) {
            paymentService.hardDeleteById(id, call.context());
            } else {
            paymentService.deleteById(id, call.context());
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
    public ResponseEntity<ApiResponse<Payment>> setActivation(
            @PathVariable UUID id,
            @RequestParam boolean active,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("id", id);
        params.put("active", active);
        TracedCall call = tracedCall(log, "set-activation", "PUT", BASE_PATH + "/{id}/activation", headers, null, params);
        try {
            Payment updated = paymentService.setActive(id, active, call.context());
            ResponseEntity<ApiResponse<Payment>> response = ok(updated, call.context());
            call.trace().success(Map.of("id", id, "active", active, "success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

}
