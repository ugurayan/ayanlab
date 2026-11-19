# Auth (artık `ayanlab-platform` içinde)

Kimlik, JWT ve platform RBAC **`ayanlab-platform/ayanlab-platform-backend`** tek JVM’de çalışır.

- Login: `POST http://localhost:8102/auth/api/login`
- RBAC: `http://localhost:8102/auth/api/users|roles|permissions|profiles|...`
- UI: `http://localhost:3102` (`ayanlab-platform-ui`)
- DB: `ayanlab_platform` / şema `core` — `PLATFORM_DB_*` (`.env.local`)

```bash
ENV=local ./scripts/up-platform.sh
```

Paylaşılan kod: `ayanlab-platform/ayanlab-platform-core`. Auth REST: `ayanlab-platform-backend` (`@AuthPlatformApi`).

Detay: [`docs/AUTH-CORE-SPLIT.md`](docs/AUTH-CORE-SPLIT.md), [`README.platform.md`](README.platform.md).

## IPMS (PMS) yetkilendirme kataloğu

Tek kaynak (şema v2, idempotent import):  
`ayanlab-platform/ayanlab-platform-backend/src/main/resources/db/authz/ipms.json`

Platform açılışında `AuthorizationCatalogLoader` (`@Order(15)`) dosyayı okur → `Permission`, `PolicyGroup`, `Role`, demo `User` tablolarına **upsert** (tekrar seed gerekmez).

Mock UI ile aynı demo hesaplar (şifre `password`):

| E-posta | Rol |
|---------|-----|
| `pm@demo.ayanlab` | PROPERTY_MANAGER |
| `accountant@demo.ayanlab` | ACCOUNTANT |
| `leasing@demo.ayanlab` | LEASING_AGENT |
| `owner@demo.ayanlab` | PROPERTY_OWNER |
| `field@demo.ayanlab` | FIELD_STAFF |

Eski `default-data.json` içindeki `ipms` legacy `ADMIN` / `admin@ipms.local` satırları kaldırıldı; IPMS rolleri tamamen katalogdan gelir.

Entegrasyon (proxy, demo seed, B2): [`docs/pms-platform-integration.md`](docs/pms-platform-integration.md).

Web mock demo turu: [`docs/pms-demo-walkthrough.md`](docs/pms-demo-walkthrough.md).