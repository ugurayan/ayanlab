package com.ayanlabs.core.controller;

import com.ayanlabs.core.annotation.ClientHeaders;
import com.ayanlabs.core.condition.CoreBusinessApi;
import com.ayanlabs.core.data.ClientHeadersPayload;
import com.ayanlabs.core.data.responses.ApiResponse;
import com.ayanlabs.core.entity.BankAccount;
import com.ayanlabs.core.service.IBankAccountService;
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
@RequestMapping("/core/api/bank-accounts")
@Tag(name = "Core · BankAccountController", description = "Profile-scoped bank accounts")
@RequiredArgsConstructor
public class BankAccountController extends BaseController {

    private static final String BASE_PATH = "/core/api/bank-accounts";

    private final IBankAccountService bankAccountService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<BankAccount>> create(
            @Valid @RequestBody BankAccount body,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "create-bank-account", "POST", BASE_PATH, headers, body, null);
        try {
            BankAccount created = bankAccountService.create(body, call.context());
            return ok(created, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PutMapping(value = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<BankAccount>> update(
            @PathVariable UUID id,
            @Valid @RequestBody BankAccount body,
            @ClientHeaders ClientHeadersPayload headers) {
        Map<String, Object> params = Map.of("id", id);
        TracedCall call = tracedCall(log, "update-bank-account", "PUT", BASE_PATH + "/{id}", headers, body, params);
        try {
            body.setId(id);
            return ok(bankAccountService.update(body, call.context()), call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<BankAccount>> getById(
            @PathVariable UUID id,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "get-bank-account", "GET", BASE_PATH + "/{id}", headers, null, Map.of("id", id));
        try {
            return ok(bankAccountService.findById(id, call.context()), call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<BankAccount>>> list(
            @RequestParam(required = false) UUID profileId,
            @RequestParam(required = false) UUID appId,
            @ClientHeaders ClientHeadersPayload headers) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("profileId", profileId);
        params.put("appId", appId);
        TracedCall call = tracedCall(log, "list-bank-accounts", "GET", BASE_PATH, headers, null, params);
        try {
            List<BankAccount> rows = bankAccountService.listByProfile(profileId, appId, call.context());
            return ok(rows, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse<Object>> delete(
            @PathVariable UUID id,
            @RequestParam(name = "hard", defaultValue = "false") boolean hard,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "delete-bank-account", "DELETE", BASE_PATH + "/{id}", headers, null, Map.of("id", id));
        try {
            if (hard) {
            bankAccountService.hardDeleteById(id, call.context());
            } else {
            bankAccountService.deleteById(id, call.context());
            }
            return ok(null, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }
}