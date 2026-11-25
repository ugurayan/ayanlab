---
name: ayanlabs-controller-headers
description: >-
  Add standard X-Client-* and X-Correlation-Id headers to every AyanLabs
  Spring controller endpoint. Use when creating or editing controllers in
  ayanlabs (core, platform, app-vault, app-crm, app-ipms, app-staffiq).
---

# AyanLabs controller client headers

## Rule

Bind headers with a single parameter:

```java
@ClientHeaders ClientHeadersPayload headers
```

Use `@ClientHeaders(requireTokens = false)` on sign-in/auth and public catalog GET endpoints.

## Required headers (enforced by resolver)

- `X-Correlation-Id`
- `X-App-Id`
- `X-Client-Type`
- `X-Client-Token` (unless `requireTokens = false`)
- `X-Client-Refresh-Token` (unless `requireTokens = false`)

Optional: `X-Client-Language`

Do **not** add per-method `@RequestHeader` for the old 10-header block.

Constants: `com.ayanlab.core.constant.ApiClientHeaders`.

## Core module pattern

- Class `extends BaseController`.
- `TracedCall call = tracedCall(log, operation, method, path, headers, body, params);`
- Services use `call.context()`; return `ok(entity, call.context())`.

## App module pattern

Same as core: `@ClientHeaders ClientHeadersPayload headers` + `tracedCall`.

## Full documentation

Read `CONTROLLER_API_HEADERS.md` at the ayanlabs repository root before editing controllers.

## Do not

- Repeat individual `@RequestHeader` lines for the standard client headers.
- Require tokens on auth register/login or public subscription catalog GET endpoints.
