package com.ayanlab.core.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.condition.AuthPlatformApi;
import com.ayanlab.core.dto.ClientHeadersPayload;
import com.ayanlab.core.dto.response.ApiResponse;
import com.ayanlab.core.entity.LoginHistory;
import com.ayanlab.core.service.ILoginHistoryService;
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
@RequestMapping(ApiPathPrefixes.AUTH + "/login-history")
@Tag(name = "Auth · LoginHistory", description = "Login audit (tmp/auth)")
@RequiredArgsConstructor
public class LoginHistoryController extends BaseController {

    private final ILoginHistoryService loginHistoryService;

    @GetMapping(value = "user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<LoginHistory>>> byUser(@PathVariable UUID userId, @ClientHeaders ClientHeadersPayload headers) {
        var call = tracedCall(log, "login-history", "GET", ApiPathPrefixes.AUTH + "/login-history", headers, null, Map.of("userId", userId));
        return ok(loginHistoryService.findByUserId(userId, call.context()), call.context());
    }
}