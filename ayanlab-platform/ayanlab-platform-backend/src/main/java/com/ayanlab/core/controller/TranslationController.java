package com.ayanlab.core.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.condition.AuthPlatformApi;
import com.ayanlab.core.dto.ClientHeadersPayload;
import com.ayanlab.core.dto.response.ApiResponse;
import com.ayanlab.core.entity.Translation;
import com.ayanlab.core.service.ITranslationService;
import com.ayanlab.core.constant.ApiPathPrefixes;
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
@RequestMapping(ApiPathPrefixes.AUTH + "/translations")
@Tag(name = "Auth · Translation", description = "App i18n messages (tmp/auth)")
@RequiredArgsConstructor
public class TranslationController extends BaseController {

    private static final String BASE = ApiPathPrefixes.AUTH + "/translations";
    private final ITranslationService translationService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Translation>> create(@RequestBody Translation body, @ClientHeaders ClientHeadersPayload headers) {
        var call = tracedCall(log, "create-translation", "POST", BASE, headers, body, null);
        return ok(translationService.create(body, call.context()), call.context());
    }

    @PutMapping(value = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Translation>> update(@PathVariable UUID id, @RequestBody Translation body, @ClientHeaders ClientHeadersPayload headers) {
        body.setId(id);
        var call = tracedCall(log, "update-translation", "PUT", BASE + "/{id}", headers, body, Map.of("id", id));
        return ok(translationService.update(body, call.context()), call.context());
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<Translation>>> list(
            @RequestParam(required = false) UUID appId,
            @RequestParam(required = false) String language,
            @ClientHeaders ClientHeadersPayload headers) {
        var call = tracedCall(log, "list-translations", "GET", BASE, headers, null, null);
        return ok(translationService.findByAppAndLanguage(appId, language, call.context()), call.context());
    }

    @DeleteMapping(value = "{id}")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable UUID id,
            @RequestParam(name = "hard", defaultValue = "false") boolean hard,
            @ClientHeaders ClientHeadersPayload headers) {
        var call = tracedCall(log, "delete-translation", "DELETE", BASE + "/{id}", headers, null, Map.of("id", id));
        if (hard) {
            translationService.hardDeleteById(id, call.context());
            } else {
            translationService.deleteById(id, call.context());
            }
        return ok(null, call.context());
    }
}