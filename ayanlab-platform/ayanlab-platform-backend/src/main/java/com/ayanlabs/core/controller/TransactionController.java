package com.ayanlabs.core.controller;

import com.ayanlabs.core.condition.CoreBusinessApi;
import com.ayanlabs.core.data.ClientHeadersPayload;

import com.ayanlabs.core.annotation.ClientHeaders;

import com.ayanlabs.core.data.enums.TransactionStatusEnum;
import com.ayanlabs.core.data.responses.ApiResponse;
import com.ayanlabs.core.entity.Transaction;
import com.ayanlabs.core.service.ITransactionService;
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
@RequestMapping("/core/api/transactions")
@Tag(name = "Core · TransactionController", description = "Payment provider transactions (idempotent)")
@RequiredArgsConstructor
public class TransactionController extends BaseController {

    private static final String BASE_PATH = "/core/api/transactions";

    private final ITransactionService transactionService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Transaction>> create(
            @Valid @RequestBody Transaction transaction,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        TracedCall call = tracedCall(log, "create-transaction", "POST", BASE_PATH, headers, transaction, null);
        try {
            Transaction created = transactionService.create(transaction, call.context());
            ResponseEntity<ApiResponse<Transaction>> response = ok(created, call.context());
            call.trace().success(Map.of("createdId", created.getId(), "success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PutMapping(value = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Transaction>> update(
            @PathVariable UUID id,
            @Valid @RequestBody Transaction transaction,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("id", id);
        TracedCall call = tracedCall(log, "update-transaction", "PUT", BASE_PATH + "/{id}", headers, transaction, params);
        try {
            transaction.setId(id);
            Transaction updated = transactionService.update(transaction, call.context());
            ResponseEntity<ApiResponse<Transaction>> response = ok(updated, call.context());
            call.trace().success(Map.of("updatedId", updated.getId(), "success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Transaction>> getById(
            @PathVariable UUID id,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("id", id);
        TracedCall call = tracedCall(log, "get-transaction", "GET", BASE_PATH + "/{id}", headers, null, params);
        try {
            Transaction found = transactionService.findById(id, call.context());
            ResponseEntity<ApiResponse<Transaction>> response = ok(found, call.context());
            call.trace().success(Map.of("foundId", found != null ? found.getId() : null, "success", found != null));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "by-idempotency-key", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Transaction>> getByIdempotencyKey(
            @RequestParam UUID appId,
            @RequestParam String idempotencyKey,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("appId", appId);
        params.put("idempotencyKey", idempotencyKey);
        TracedCall call = tracedCall(log, "get-transaction-by-idempotency-key", "GET", BASE_PATH + "/by-idempotency-key", headers, null, params);
        try {
            Transaction found = transactionService.findByIdempotencyKey(appId, idempotencyKey).orElse(null);
            ResponseEntity<ApiResponse<Transaction>> response = ok(found, call.context());
            call.trace().success(Map.of("foundId", found != null ? found.getId() : null, "success", found != null));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<Transaction>>> list(
            @RequestParam UUID appId,
            @RequestParam(required = false) TransactionStatusEnum status,
            @RequestParam(required = false) UUID profileId,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("appId", appId);
        params.put("status", status);
        params.put("profileId", profileId);
        TracedCall call = tracedCall(log, "list-transactions", "GET", BASE_PATH, headers, null, params);
        try {
            List<Transaction> rows = profileId != null
                    ? transactionService.listByProfile(profileId)
                    : transactionService.listByApp(appId, status);
            ResponseEntity<ApiResponse<List<Transaction>>> response = ok(rows, call.context());
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
        TracedCall call = tracedCall(log, "delete-transaction", "DELETE", BASE_PATH + "/{id}", headers, null, params);
        try {
            if (hard) {
            transactionService.hardDeleteById(id, call.context());
            } else {
            transactionService.deleteById(id, call.context());
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
    public ResponseEntity<ApiResponse<Transaction>> setActivation(
            @PathVariable UUID id,
            @RequestParam boolean active,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("id", id);
        params.put("active", active);
        TracedCall call = tracedCall(log, "set-activation", "PUT", BASE_PATH + "/{id}/activation", headers, null, params);
        try {
            Transaction updated = transactionService.setActive(id, active, call.context());
            ResponseEntity<ApiResponse<Transaction>> response = ok(updated, call.context());
            call.trace().success(Map.of("id", id, "active", active, "success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

}
