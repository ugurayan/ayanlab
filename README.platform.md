# AyanLab Platform (`ayanlab-platform`)

Tüm ürünlerin paylaştığı **platform**: uygulama kataloğu, RBAC, abonelik, fatura/ödeme, bildirim ve entegrasyonlar.

## Sorumluluklar

### Platform & tenancy

- **App** — `code`, `apiBasePath`, `subscriptionMode`, `multiTenantEnabled`, `moduleAuthEnabled`
- **Module**, **ApplicationModule**, **ApplicationPermission** (rolün app'i `Role.appId`)
- **Organization**, **OrganizationUser**, org detayları
- **UserProfile** — çok profilli model (`profileSlug`, `tenancy`, `primaryUserTypeId`)
- **UserDetail** — profil başına key/value ek alanlar (app özel veri User’dan ayrı)

### Authorization

- **Role**, **Permission**, **PolicyGroup**, join tabloları
- **ProfileRole**, **ProfilePermission**, **ProfilePolicyGroup**
- **AuthorizationResolverService** → `EffectiveAuthorization`
- Permission kodu: `{app}:{resource}:{action}`; grant'larda `*` (ör. `staffiq:shifts:*`, `ipms:*:*`)
- Rol'ler app-scoped (`Role.appId`)
- Rol → PolicyGroup → Permission tek yol; doğrudan rol izni (`RolePermission`) ve `ApplicationRole` kaldırıldı. Eski tablolar açılış migrasyonunda `*_legacy` olarak yeniden adlandırılır (silinmez).
- Seed: `db/authz/{core,ipms}.json` (catalog v2), `apps-catalog.json`
- App-owned catalog: HRM, CRM, Vault, Booking, Comm `src/main/resources/authz/*.json` dosyalarını açılışta `POST /core/api/internal/authorization-catalogs` ile yayınlar (header `X-Internal-Token` = `PLATFORM_INTERNAL_TOKEN`; kapatmak için `AUTHZ_CATALOG_PUBLISH=false`)
- Token iptali: `App.authzVersion` / `UserProfile.authzVersion` yetki değişiminde artar; token `av` claim'i eskiyse istek yetkisiz işlenir ve yanıtta `X-Authz-Stale: true` döner (istemci `/auth/api/refresh` çağırır). App backend'leri `GET /core/api/internal/authz-versions` ile kontrol eder (`AUTHZ_VERSION_CHECK_MODE=remote`)

### Subscription & billing

- **SubscriptionPlan**, **ProfileSubscription**, **OrganizationSubscription**
- **Invoice**, **Payment** (`profileId`, `profileSubscriptionId`), **Transaction**
- Org-level ve user/profile-level planlar (`AppSubscriptionModeEnum`)

### Integrations (tüm app’ler kullanır)

| Servis | Paket / sınıf |
|--------|----------------|
| Email | `EmailDispatchService`, templates |
| SMS | `TwilioSmsService` |
| Kafka | `spring-kafka` producers/consumers |
| GCP Storage | `CloudFile`, signed URLs |
| Notifications | `NotificationTemplate` |

### Ürün haritası

| `appCode` | Deploy | Personalar (örnek) |
|-----------|--------|---------------------|
| `ipms` | `ayanlab-pms` | site manager, tenant, vendor, owner |
| `crm` | `ayanlab-crm` | org admin, staff, customer, vendor |
| `vault` | `ayanlab-vault` | personal, org member |
| `staffiq` | `ayanlab-hrm` | worker, hiring mgr (InstaWork) |
| `booking` | `ayanlab-booking` | provider, customer |
| `comm` | `ayanlab-comm` | school admin, parent, student, staff (i18n SMS/email) |

## API

- Tek JVM: `ayanlab-platform-backend` — `http://localhost:8102/auth/api/**` (kimlik + RBAC) ve `http://localhost:8102/core/api/**` (iş verisi, `X-Profile-Id`)
- UI: `ayanlab-platform-ui` → `http://localhost:3102`
- DB: `ayanlab_platform` / şema `core` (`PLATFORM_DB_*`)

## Deploy

```bash
ENV=local ./scripts/up-foundation.sh   # veya core compose
cd ayanlab-platform && ENV=local ./run-local.sh          # menü: backend | ui | vault | …
# veya: ./run-local.sh backend | ./run-local.sh ui
```

## Kod yapısı (hedef bütünlük)

```
ayanlab-platform/ayanlab-platform-core/     → DTO, JWT, güvenlik
ayanlab-platform/ayanlab-platform-backend/
  platform/startup/           → DefaultDataLoader, AuthorizationCatalogLoader
  src/main/resources/db/      → JSON seed, SQL migrations
```

Ürün backend’leri `ayanlab-platform-core` JAR’ına bağlanır; domain REST yalnızca `ayanlab-platform-backend` üzerindedir.

## Profile-centric veri

- Ödeme / kart / banka: `Payment.profileId`, `UserDetail` (`detailKey` = `bank_account`, `card_token`, …).
- Modül yetkisi: permission code = `{app}.{module}.{action}` (`PermissionCodes`).

## Veritabanı

- `ayanlab_platform` DB, şema `core` (bkz. [`docs/DATABASES.md`](docs/DATABASES.md))

## Legacy `tmp/auth` migrasyonu

| Eski | Core’da |
|------|---------|
| Applications.json subscription | `App.subscriptionMode` |
| User* / Org* join matrisi | Profile + catalog loader |
| Modül listesi | `Module` + `ApplicationModule` + IPMS catalog |
| UserGroup (eksik) | PolicyGroup + UserType persona |

Swagger: [`docs/API-SWAGGER.md`](docs/API-SWAGGER.md).