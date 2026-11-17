# OpenAPI / Swagger UI

Tüm Maven backend’ler **[springdoc-openapi](https://springdoc.org/)** kullanır (`springdoc-openapi-starter-webmvc-ui`).

| Servis | Lokal port | Swagger UI | OpenAPI JSON |
|--------|------------|------------|--------------|
| auth | 8101 | http://localhost:8101/swagger-ui/index.html | http://localhost:8101/v3/api-docs |
| core | 8102 | http://localhost:8102/swagger-ui/index.html | http://localhost:8102/v3/api-docs |
| vault | 8103 | http://localhost:8103/swagger-ui/index.html | http://localhost:8103/v3/api-docs |
| hrm | 8104 | http://localhost:8104/swagger-ui/index.html | http://localhost:8104/v3/api-docs |
| pms | 8105 | http://localhost:8105/swagger-ui/index.html | http://localhost:8105/v3/api-docs |
| crm | 8106 | http://localhost:8106/swagger-ui/index.html | http://localhost:8106/v3/api-docs |
| booking | 8107 | http://localhost:8107/swagger-ui/index.html | http://localhost:8107/v3/api-docs |
| comm | 8108 | http://localhost:8108/swagger-ui/index.html | http://localhost:8108/v3/api-docs |

`/swagger-ui.html` çoğu serviste `/swagger-ui/index.html` adresine yönlendirir. Spring Security’de `/swagger-ui/**`, `/v3/api-docs/**` ve `/webjars/**` public olmalıdır (`ayanlab-platform-core` `SecurityConfig`).

Docker içinde backend’ler `8080` dinler; host portu ürün `docker-compose.yml` / [`docs/PORTS.md`](PORTS.md) ile eşlenir.

## JWT

Swagger’da **Authorize** → `bearerAuth` → yalnızca access token (Bearer öneki ekleme).

Public uçlar (`/auth/api/**`, `/*/api/auth/**`, seçili `/pms/api/...`, swagger, actuator) — bkz. [`API-PATHS.md`](API-PATHS.md).

## Kod

- Bağımlılık: her `ayanlab-*-backend/pom.xml` + `ayanlab-platform-core`
- Ortak `OpenAPI` bean: `ayanlab-platform-core` `OpenApiConfig` (`com.ayanlabs.core` component scan)
- `application.yml`: `springdoc.api-docs.path`, `springdoc.swagger-ui.path`

Yeni backend üretirken: `./scripts/generate-maven-backends.sh` şablonda springdoc vardır.