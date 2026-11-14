# API curl örnekleri

Platform çalışırken (`./gradlew :platform:bootRun` veya Docker) her modülü hızlıca doğrulamak için modül bazlı curl örnekleri.

## Hızlı başlangıç

```bash
# 1) Ortam değişkenleri (repo kökündeki .env)
set -a && source .env && set +a
# veya: bash scripts/run-platform.sh

# 2) Token al (Vault register örneği)
# docs/curl/vault.md içindeki register curl'unu çalıştırın, sonra:
export ACCESS_TOKEN="<accessToken>"
export REFRESH_TOKEN="<refreshToken>"

# 3) Tüm modüller için smoke test
bash docs/curl/smoke-test.sh
```

## Modül dosyaları

| Modül | Dosya | Açıklama |
|-------|-------|----------|
| Ortak | [env.example.sh](./env.example.sh) | `BASE_URL`, app UUID'leri, header helper'ları |
| Core | [core.md](./core.md) | Platform CRUD + authorization + assignments |
| IPMS | [ipms.md](./ipms.md) | Property management |
| CRM | [crm.md](./crm.md) | Accounting |
| Vault | [vault.md](./vault.md) | Auth + vault |
| StaffIQ | [staffiq.md](./staffiq.md) | Rideshare |
| Platform | [platform.md](./platform.md) | Swagger, actuator |

## Zorunlu header'lar

Tüm endpoint'ler `CONTROLLER_API_HEADERS.md` kurallarını kullanır:

| Header | Zorunlu | Not |
|--------|---------|-----|
| `X-Correlation-Id` | evet | UUID |
| `X-App-Id` | evet | Uygulama UUID |
| `X-Client-Type` | evet | `web`, `mobile`, … |
| `X-Client-Token` | auth'da evet | Access token |
| `X-Client-Refresh-Token` | auth'da evet | Refresh token |
| `Authorization` | auth'da evet | `Bearer <accessToken>` |

Auth / public catalog endpoint'lerinde `@ClientHeaders(requireTokens = false)` — token header'ları opsiyonel.

## Seed kullanıcılar (default-data.json)

| App | Email | Şifre |
|-----|-------|-------|
| core | admin@core.local | Admin123! |
| ipms | admin@ipms.local | Admin123! |
| crm | admin@crm.local | Admin123! |
| vault | admin@vault.local | Admin123! |
| staffiq | admin@staffiq.local | Admin123! |

> Not: `login` endpoint'i henüz tam implemente değil; token almak için **register** veya mevcut JWT kullanın.
