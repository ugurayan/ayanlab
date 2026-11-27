package com.ayanlab.core.authz.cache;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.controller.BaseController;
import com.ayanlab.core.dto.ClientHeadersPayload;
import com.ayanlab.core.dto.response.ApiResponse;
import com.ayanlab.core.provider.AppSecurityContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("${ayanlabs.authz-cache.api-base:/auth/api}/authorization")
@RequiredArgsConstructor
public class ProfileAuthzSnapshotController extends BaseController {

    private final ProfileAuthzCacheService profileAuthzCacheService;

    @GetMapping(value = "/snapshot", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ProfileAuthzSnapshot>> snapshot(@ClientHeaders ClientHeadersPayload headers) {
        String path = "${ayanlabs.authz-cache.api-base:/auth/api}/authorization/snapshot";
        TracedCall call = tracedCall(log, "authorization-snapshot", "GET", path, headers, null, null);
        try {
            AppSecurityContext.AppContext security = AppSecurityContext.get();
            String tokenVersion = security != null ? security.getAuthzVersion() : null;
            ProfileAuthzSnapshot data = profileAuthzCacheService.resolve(call.context(), tokenVersion);
            ResponseEntity<ApiResponse<ProfileAuthzSnapshot>> response = ok(data, call.context());
            call.trace().success(Map.of(
                    "permissionCount", data.permissions() != null ? data.permissions().size() : 0,
                    "success", true));
            return response;
        } catch (Exception ex) {
            call.trace().error(ex);
            throw ex;
        }
    }
}
