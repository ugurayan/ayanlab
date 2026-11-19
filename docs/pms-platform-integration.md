# PMS ↔ Platform entegrasyonu (B0.3 / B2)

## Portlar

| Servis | Port | UI |
|--------|------|-----|
| `ayanlab-platform-backend` | **8102** | 3102 |
| `ayanlab-pms-backend` | **8105** | 3105 |

Bkz. [`PORTS.md`](PORTS.md), [`API-PATHS.md`](API-PATHS.md).

## Abonelik modu

`ipms` uygulaması platform `apps-catalog` / `default-data.json` içinde `ORGANIZATION_AND_USER` — hem org hem kullanıcı aboneliği gerekir (B0.4).

## Auth akışı

1. **Login:** `POST http://localhost:8102/auth/api/login` (app `ipms`, kullanıcı `pm@demo.ayanlab` / `password`).
2. JWT içinde `ipms:*` permission kodları (AuthZ v2 kataloğu: `ayanlab-platform-backend/src/main/resources/db/authz/ipms.json`).
3. PMS API çağrıları: `Authorization: Bearer …` + `X-App-Id` + `X-Client-Type` + `X-Correlation-Id`.

## UI dev proxy (`ayanlab-pms-ui`)

`vite.config.ts`:

- `/pms/api` → `http://localhost:8105`
- `/auth` → `http://localhost:8102`

`.env` (B2):

```env
VITE_MOCK_ONLY=false
VITE_PMS_API_BASE_URL=/pms/api
VITE_AUTH_API_BASE_URL=
```

## PMS backend demo seed

Platform’da `demo_ayanlab` org oluştuktan sonra org UUID’sini alın, sonra:

```bash
export IPMS_LOAD_DEMO_DATA=true
export IPMS_DEMO_ORG_ID=<platform-org-uuid>
```

Kaynak: `ayanlab-pms-backend/src/main/resources/db/ipms-demo-seed.json` (Panorama rental + HOA + TR modül örnekleri: ATS, kargo, tur, vb.).

## Platform admin — IPMS izinleri (B0.5)

`ayanlab-platform-ui` → **Applications** → **IPMS permissions** / **IPMS roles** (filtreli liste, salt okunur).

## Marketplace billing stub (B0.6)

`GET /pms/api/marketplace/billing-bridge` — platform fatura API yolu + addon sayısı.

## WhatsApp org config (B0.7)

`GET/POST /pms/api/org-whatsapp-settings` — org başına kanal stub (`OrgWhatsappChannelSetting`).

## Yetkilendirme

- Katalog: tek kaynak `db/authz/ipms.json` (idempotent `AuthorizationCatalogLoader`).
- CRUD: `IpmsCrudController` → `IpmsAuthz.requireOperation(prefix, action)`.
- Dashboard / activity: `@RequirePermission("ipms:dashboard:read")`.