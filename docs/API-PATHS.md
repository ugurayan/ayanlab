# API path convention

Her deploy edilen backend kendi kök önekiyle yayınlanır:

| Ürün | Önek | Örnek |
|------|------|--------|
| auth | `/auth/api` | `POST /auth/api/login` |
| core | `/core/api` | `GET /core/api/users` |
| vault | `/vault/api` | `GET /vault/api/me` |
| crm | `/crm/api` | `GET /crm/api/...` |
| hrm | `/hrm/api` | `GET /hrm/api/health` |
| pms | `/pms/api` | `GET /pms/api/health` |
| booking | `/booking/api` | (stub) |
| comm | `/comm/api` | (stub) |

Eski `/ayanlabs/api/...` ve `/api/core/...` kullanımı kaldırıldı.

Swagger her serviste aynı host portunda: `/swagger-ui/index.html` — bkz. [`API-SWAGGER.md`](API-SWAGGER.md).

Sabitler: `com.ayanlab.core.web.ApiPathPrefixes`