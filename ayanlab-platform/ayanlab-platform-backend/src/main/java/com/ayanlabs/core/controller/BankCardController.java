package com.ayanlabs.core.controller;

import com.ayanlabs.core.annotation.ClientHeaders;
import com.ayanlabs.core.condition.CoreBusinessApi;
import com.ayanlabs.core.data.ClientHeadersPayload;
import com.ayanlabs.core.data.responses.ApiResponse;
import com.ayanlabs.core.entity.BankCard;
import com.ayanlabs.core.service.IBankCardService;
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
@RequestMapping("/core/api/bank-cards")
@Tag(name = "Core · BankCardController", description = "Profile-scoped payment cards")
@RequiredArgsConstructor
public class BankCardController extends BaseController {

    private static final String BASE_PATH = "/core/api/bank-cards";

    private final IBankCardService bankCardService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<BankCard>> create(
            @Valid @RequestBody BankCard body,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "create-bank-card", "POST", BASE_PATH, headers, body, null);
        try {
            return ok(bankCardService.create(body, call.context()), call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PutMapping(value = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<BankCard>> update(
            @PathVariable UUID id,
            @Valid @RequestBody BankCard body,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "update-bank-card", "PUT", BASE_PATH + "/{id}", headers, body, Map.of("id", id));
        try {
            body.setId(id);
            return ok(bankCardService.update(body, call.context()), call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<BankCard>> getById(
            @PathVariable UUID id,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "get-bank-card", "GET", BASE_PATH + "/{id}", headers, null, Map.of("id", id));
        try {
            return ok(bankCardService.findById(id, call.context()), call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<BankCard>>> list(
            @RequestParam(required = false) UUID profileId,
            @RequestParam(required = false) UUID appId,
            @ClientHeaders ClientHeadersPayload headers) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("profileId", profileId);
        params.put("appId", appId);
        TracedCall call = tracedCall(log, "list-bank-cards", "GET", BASE_PATH, headers, null, params);
        try {
            return ok(bankCardService.listByProfile(profileId, appId, call.context()), call.context());
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
        TracedCall call = tracedCall(log, "delete-bank-card", "DELETE", BASE_PATH + "/{id}", headers, null, Map.of("id", id));
        try {
            if (hard) {
            bankCardService.hardDeleteById(id, call.context());
            } else {
            bankCardService.deleteById(id, call.context());
            }
            return ok(null, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }
}