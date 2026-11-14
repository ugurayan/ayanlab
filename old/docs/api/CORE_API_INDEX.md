# Core API Index

Base: `${BASE_URL}/api/core`  
Headers: [CONTROLLER_API_HEADERS.md](../../CONTROLLER_API_HEADERS.md)  
Curl helpers: [curl/env.example.sh](../curl/env.example.sh)

| Doküman | Kapsam |
|---------|--------|
| [CORE_PLATFORM_API.md](./CORE_PLATFORM_API.md) | Apps, organizations, modules, users, profiles, user-types, templates |
| [CORE_AUTHORIZATION_API.md](./CORE_AUTHORIZATION_API.md) | Permissions, roles, policy groups, effective authorization |
| [CORE_ASSIGNMENTS_API.md](./CORE_ASSIGNMENTS_API.md) | App/profile/role/permission mapping tabloları |
| [CORE_BILLING_API.md](./CORE_BILLING_API.md) | Invoices, payments, transactions, subscriptions |

## Tüm controller endpoint özeti

| Resource | Path | LIST | GET | POST | PUT | DELETE |
|----------|------|:----:|:---:|:----:|:---:|:------:|
| Apps | `/apps` | ✓ | ✓ | ✓ | ✓ | ✓ |
| Organizations | `/organizations` | ✓ | ✓ | ✓ | ✓ | ✓ |
| Modules | `/modules` | ✓ | ✓ | ✓ | ✓ | ✓ |
| Users | `/users` | ✓ | ✓ | ✓ | ✓ | ✓ |
| Profiles | `/profiles` | ✓ | ✓ | ✓ | ✓ | ✓ |
| User types | `/user-types` | ✓ | ✓ | ✓ | ✓ | ✓ |
| Templates | `/templates` | ✓ | ✓ | ✓ | ✓ | ✓ |
| Permissions | `/permissions` | ✓ | ✓ | ✓ | ✓ | ✓ |
| Roles | `/roles` | ✓ | ✓ | ✓ | ✓ | ✓ |
| Policy groups | `/policy-groups` | ✓ | ✓ | ✓ | ✓ | ✓ |
| Role permissions | `/role-permissions` | ✓ | ✓ | ✓ | ✓ | ✓ |
| Role policy groups | `/role-policy-groups` | ✓ | ✓ | ✓ | ✓ | ✓ |
| Policy group permissions | `/policy-group-permissions` | ✓ | ✓ | ✓ | ✓ | ✓ |
| Application roles | `/application-roles` | ✓ | ✓ | ✓ | ✓ | ✓ |
| Application permissions | `/application-permissions` | ✓ | ✓ | ✓ | ✓ | ✓ |
| Profile roles | `/profile-roles` | ✓ | ✓ | ✓ | ✓ | ✓ |
| Profile permissions | `/profile-permissions` | ✓ | ✓ | ✓ | ✓ | ✓ |
| Profile policy groups | `/profile-policy-groups` | ✓ | ✓ | ✓ | ✓ | ✓ |
| Profile resource scopes | `/profile-resource-scopes` | ✓ | ✓ | ✓ | ✓ | ✓ |
| Authorization | `/authorization/effective` | — | ✓ | — | — | — |
| Invoices | `/invoices` | ✓ | ✓ | ✓ | ✓ | ✓ |
| Invoice line items | `/invoice-line-items` | ✓ | ✓ | ✓ | ✓ | ✓ |
| Payments | `/payments` | ✓ | ✓ | ✓ | ✓ | ✓ |
| Transactions | `/transactions` | ✓ | ✓ | ✓ | ✓ | ✓ |
| Subscription plans | `/subscription-plans` | ✓ | ✓ | ✓ | ✓ | ✓ |
| Plan features | `/subscription-plan-features` | ✓ | ✓ | ✓ | ✓ | ✓ |
| Plan prices | `/subscription-plan-prices` | ✓ | ✓ | ✓ | ✓ | ✓ |
| Profile subscriptions | `/profile-subscriptions` | ✓ | ✓ | start* | — | ✓ |

\* `POST /profile-subscriptions/start` — abonelik başlatma (PUT yok, lifecycle endpoint)
