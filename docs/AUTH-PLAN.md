# ayanlab-platform — güncel plan

## Sorumluluk

Tek **kimlik ve yetki** servisi:

- Authentication (login, register, refresh, logout, SSO hazırlığı)
- Authorization (roller / permission çözümleme)
- JWT üretimi ve doğrulama (diğer servisler auth API çağırır)

**ayanlab-platform-ui** yalnızca kullanıcı yönetimi arayüzüdür; ürün UI’larından ayrıdır.

## Veritabanı

| Servis | DB adı | Schema (varsayılan) |
|--------|--------|---------------------|
| ayanlab-platform-backend | `ayanlabAuth` | `auth` |
| ayanlab-platform-backend | `ayanlabCore` | `core` |

Diğer ürünler kendi DB’lerini kullanır; **userId, profileId, appId, orgId** gibi kimlikler auth/core sözleşmesiyle taşınır (JWT claim + header).

## Diğer projeler

HTTP only — örnek:

```http
Authorization: Bearer <access_token>
x-app-id: <uuid>
x-profile-id: <uuid>
```

Token ve yetki: **ayanlab-platform-backend** (`AYANLAB_AUTH_URL`).

Domain verisi: **ayanlab-platform-backend** (`AYANLAB_CORE_URL`).

## İlk ayağa kaldırma (sadece auth backend)

```bash
cp .env.example .env
# JWT_SECRET=…  POSTGIS_PASSWORD=…
./scripts/up-auth.sh
```

Platform tek JVM: `ayanlab-platform-backend`; tablolar `ayanlab_platform` / şema `core` üzerinde Hibernate `ddl-auto: update` ile oluşur.

**Profil modeli:** çoklu `UserProfile` (`profileSlug`), JWT `pid` — bkz. kök [`README.auth.md`](../README.auth.md) ve [`README.platform.md`](../README.platform.md).

Sonraki adım: auth DB’de yalnızca User + oturum; profile/RBAC HTTP ile core.

## Mesajın yarım kaldı

“mevcutta …” diye devam edeceksen: auth’ta kullanmak istediğin endpoint listesi, SSO sağlayıcısı veya mevcut VPS DB adı varsa yaz; compose/env’yi ona göre netleştiririz.