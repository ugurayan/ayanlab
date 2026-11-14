# AyanLabs Documentation

Tek dokümantasyon kökü: `docs/`.

| Klasör | İçerik |
|--------|--------|
| [api/](./api/) | Modül API rehberleri (Core, IPMS, Vault, …) |
| [curl/](./curl/README.md) | Çalıştırılabilir curl örnekleri ve smoke test |
| [controllers/](./controllers/) | Controller bazlı endpoint + örnek response referansı |

## Hızlı başlangıç

```bash
source docs/curl/env.example.sh
bash docs/curl/smoke-test.sh
```

## API rehberleri

| Dosya | Açıklama |
|-------|----------|
| [api/CORE_API_INDEX.md](./api/CORE_API_INDEX.md) | Core API endpoint özeti |
| [api/CORE_PLATFORM_API.md](./api/CORE_PLATFORM_API.md) | Apps, orgs, modules, users, profiles |
| [api/CORE_AUTHORIZATION_API.md](./api/CORE_AUTHORIZATION_API.md) | Permissions, roles, policy groups |
| [api/CORE_ASSIGNMENTS_API.md](./api/CORE_ASSIGNMENTS_API.md) | App/profile/role/permission mapping |
| [api/CORE_BILLING_API.md](./api/CORE_BILLING_API.md) | Invoices, payments, subscriptions |
| [api/IPMS_API.md](./api/IPMS_API.md) | IPMS CRUD + signup akışı |
| [api/IPMS_AUTHORIZATION.md](./api/IPMS_AUTHORIZATION.md) | IPMS permission matrix / seed roller |
| [api/VAULT_SUBSCRIPTION_CATALOG.md](./api/VAULT_SUBSCRIPTION_CATALOG.md) | Vault abonelik kataloğu |

## Ortak kurallar

API header kuralları: [CONTROLLER_API_HEADERS.md](../CONTROLLER_API_HEADERS.md) (repo kökü).
