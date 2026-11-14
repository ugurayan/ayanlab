# Controller API client headers

Every REST endpoint in **ayanlabs** binds standard client headers via `@ClientHeaders` into `ClientHeadersPayload` (resolved by `ClientHeadersArgumentResolver`).

## Header rules

| Header | Required | Notes |
|--------|----------|-------|
| `X-Correlation-Id` | yes | Request trace id |
| `X-App-Id` | yes | Application UUID |
| `X-Client-Type` | yes | e.g. `web`, `mobile`, `ios`, `android` |
| `X-Client-Token` | yes* | Access token |
| `X-Client-Refresh-Token` | yes* | Refresh token |
| `X-Client-Language` | no | e.g. `tr`, `en` |

\* Use `@ClientHeaders(requireTokens = false)` on **sign-in / auth** endpoints and **public catalog** GET endpoints (no session yet).

`userId`, `profileId`, and `orgId` are no longer passed as headers; they are taken from the JWT via `AppSecurityContext` when present.

Constants: `com.ayanlabs.core.helper.ApiClientHeaders`.

## Java template

```java
@GetMapping("/example")
public ResponseEntity<ApiResponse<Example>> example(
        @PathVariable UUID id,
        @ClientHeaders ClientHeadersPayload headers) {
    TracedCall call = tracedCall(log, "example", "GET", BASE_PATH + "/{id}", headers, null, Map.of("id", id));
    try {
        Example data = service.find(id, call.context());
        ResponseEntity<ApiResponse<Example>> response = ok(data, call.context());
        call.trace().success(Map.of("success", true));
        return response;
    } catch (Exception e) {
        call.trace().error(e);
        throw e;
    }
}
```

### Auth / public catalog (tokens optional)

```java
@PostMapping("/login")
public ResponseEntity<ApiResponse<TokenResponse>> login(
        @ClientHeaders(requireTokens = false) ClientHeadersPayload headers,
        @Valid @RequestBody LoginRequest request) {
    TracedCall call = tracedCall(log, "login", "POST", BASE_PATH + "/login", headers, request, null);
    // ...
}
```

## curl example

```bash
curl -s "http://localhost:8080/api/core/roles/{id}" \
  -H "Authorization: Bearer TOKEN" \
  -H "X-App-Id: 0cd4d187-f53e-4ebc-94cd-a601ab467094" \
  -H "X-Correlation-Id: $(uuidgen)" \
  -H "X-Client-Type: mobile" \
  -H "X-Client-Token: ACCESS_TOKEN" \
  -H "X-Client-Refresh-Token: REFRESH_TOKEN" \
  -H "X-Client-Language: tr"
```

## Checklist for new controllers

1. Extend `BaseController`.
2. Add `@ClientHeaders ClientHeadersPayload headers` (or `requireTokens = false` on auth/catalog).
3. Call `tracedCall(..., headers, body, params)`.
4. Use `call.context()` for service calls; return `ok(data, call.context())`.

Agent skill: `.cursor/skills/ayanlabs-controller-headers/SKILL.md`.
