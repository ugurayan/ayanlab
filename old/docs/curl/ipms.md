# IPMS (Property Management) — curl örnekleri

Base: `${BASE_URL}/ayanlabs/api/property-mgmt`  
App ID: `APP_ID_IPMS` = `ad94b2d2-5022-4818-a785-55807ab139ba`

Önce: `source docs/curl/env.example.sh` ve token ayarlayın.

---

## Public — health (token gerekmez)

```bash
curl -s "${BASE_URL}/ayanlabs/api/property-mgmt/health" \
  $(ayanlabs_headers_public "${APP_ID_IPMS}") | jq .
```

---

## Public — subscription planları

```bash
curl -s "${BASE_URL}/ayanlabs/api/propertymgmt/subscriptions/plans" \
  $(ayanlabs_headers_public "${APP_ID_IPMS}") | jq .
```

---

## Auth — efektif yetkiler (property manager örneği)

Register/login sonrası `manager@ipm-app.com` token'ı ile:

```bash
curl -s "${BASE_URL}/api/core/authorization/effective" \
  $(ayanlabs_headers_auth "${APP_ID_IPMS}") | jq '.data | {roleCodes, permissionCount: (.permissionCodes | length), policyGroups}'
```

Seed kullanıcılar ve roller: [IPMS_AUTHORIZATION.md](../api/IPMS_AUTHORIZATION.md)

---

## Auth — authorization context (JWT principal)

```bash
curl -s "${BASE_URL}/ayanlabs/api/property-mgmt/authorization/context" \
  $(ayanlabs_headers_auth "${APP_ID_IPMS}") | jq .
```

---

## Auth — property types listesi

Sistem varsayılanları + organizasyona özel tipler (JWT gerekir):

```bash
curl -s "${BASE_URL}/ayanlabs/api/property-mgmt/property-types" \
  $(ayanlabs_headers_auth "${APP_ID_IPMS}") | jq .
```

Tek kayıt:

```bash
PROPERTY_TYPE_ID="<uuid-from-list>"
curl -s "${BASE_URL}/ayanlabs/api/property-mgmt/property-types/${PROPERTY_TYPE_ID}" \
  $(ayanlabs_headers_auth "${APP_ID_IPMS}") | jq .
```

Organizasyona özel tip oluşturma (`SYSTEM_ADMINISTRATOR`, `ORGANIZATION_ADMINISTRATOR`, `PROPERTY_MANAGER`):

```bash
curl -s -X POST "${BASE_URL}/ayanlabs/api/property-mgmt/property-types" \
  $(ayanlabs_headers_auth "${APP_ID_IPMS}") \
  -H "Content-Type: application/json" \
  -d '{
    "code": "CUSTOM_MIXED",
    "name": "Custom Mixed Use",
    "category": "COMMERCIAL",
    "description": "Org-specific type",
    "sortOrder": 900
  }' | jq .
```

---

## Auth — properties listesi

```bash
curl -s "${BASE_URL}/ayanlabs/api/property-mgmt/properties" \
  $(ayanlabs_headers_auth "${APP_ID_IPMS}") | jq .
```

---

## Auth — property oluştur (POST örneği)

```bash
PROPERTY_TYPE_ID="<uuid-for-MULTI_FAMILY-from-property-types-list>"
curl -s -X POST "${BASE_URL}/ayanlabs/api/property-mgmt/properties" \
  $(ayanlabs_headers_auth "${APP_ID_IPMS}") \
  -H "Content-Type: application/json" \
  -d "{
    \"name\": \"Sunset Apartments\",
    \"propertyTypeId\": \"${PROPERTY_TYPE_ID}\",
    \"addressLine1\": \"100 Main St\",
    \"city\": \"Austin\",
    \"state\": \"TX\",
    \"zipCode\": \"78701\"
  }" | jq .
```

---

## Sign Up — çok adımlı kayıt akışı

Base: `${BASE_URL}/ayanlabs/api/property-mgmt/signup`

### Adım 1 — Email / telefon müsaitlik kontrolü (public)

```bash
curl -s "${BASE_URL}/ayanlabs/api/property-mgmt/signup/step-1/check-email?email=ayse@example.com" \
  $(ayanlabs_headers_public "${APP_ID_IPMS}") | jq .

curl -s "${BASE_URL}/ayanlabs/api/property-mgmt/signup/step-1/check-phone?phone=%2B15555550123" \
  $(ayanlabs_headers_public "${APP_ID_IPMS}") | jq .
```

Örnek yanıt: `{"success":true,"data":{"field":"email","value":"ayse@example.com","available":true}}`

### Adım 1 — Kişisel kayıt (public)

```bash
SIGNUP=$(curl -s -X POST "${BASE_URL}/ayanlabs/api/property-mgmt/signup/step-1/register" \
  $(ayanlabs_headers_public "${APP_ID_IPMS}") \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Ayse",
    "lastName": "Yilmaz",
    "email": "ayse.yilmaz@example.com",
    "phone": "+15555550123",
    "password": "SecurePass123!"
  }')

echo "$SIGNUP" | jq .
export ACCESS_TOKEN=$(echo "$SIGNUP" | jq -r '.data.accessToken')
export REFRESH_TOKEN=$(echo "$SIGNUP" | jq -r '.data.refreshToken')
```

### Adım 1 — Email ve mobil doğrulama

```bash
curl -s -X POST "${BASE_URL}/ayanlabs/api/property-mgmt/signup/step-1/verify-email" \
  $(ayanlabs_headers_auth "${APP_ID_IPMS}") \
  -H "Content-Type: application/json" \
  -d '{"code": "ABC123"}' | jq .

curl -s -X POST "${BASE_URL}/ayanlabs/api/property-mgmt/signup/step-1/verify-mobile" \
  $(ayanlabs_headers_auth "${APP_ID_IPMS}") \
  -H "Content-Type: application/json" \
  -d '{"code": "123456"}' | jq .
```

### Oturum durumu

```bash
curl -s "${BASE_URL}/ayanlabs/api/property-mgmt/signup/session" \
  $(ayanlabs_headers_auth "${APP_ID_IPMS}") | jq '.data | {currentStep, status, nextStep, accountCategory}'
```

### Adım 2 — Profil tipi seçimi

```bash
# PERSONAL veya ORGANIZATION
curl -s -X POST "${BASE_URL}/ayanlabs/api/property-mgmt/signup/step-2/profile-type" \
  $(ayanlabs_headers_auth "${APP_ID_IPMS}") \
  -H "Content-Type: application/json" \
  -d '{"accountCategory": "PERSONAL"}' | jq .
```

### Adım 3a — Kişisel hesap (şirket ilişkisi yok)

```bash
curl -s -X POST "${BASE_URL}/ayanlabs/api/property-mgmt/signup/step-3/personal" \
  $(ayanlabs_headers_auth "${APP_ID_IPMS}") \
  -H "Content-Type: application/json" \
  -d '{"personalAccountType": "TENANT_RENTER"}' | jq .
```

### Adım 3b — Yeni organizasyon (kayıt yapan admin olur)

```bash
curl -s -X POST "${BASE_URL}/ayanlabs/api/property-mgmt/signup/step-3/organization/new-company" \
  $(ayanlabs_headers_auth "${APP_ID_IPMS}") \
  -H "Content-Type: application/json" \
  -d '{
    "companyType": "REAL_ESTATE",
    "companyLegalName": "Ayan Property Mgmt LLC",
    "workspaceShortCode": "ayanpm",
    "companyEmail": "info@ayanpm.com",
    "companyPhone": "+15555550999",
    "website": "https://ayanpm.com",
    "addressLine1": "100 Main St",
    "city": "Austin",
    "state": "TX",
    "zipCode": "78701",
    "country": "United States"
  }' | jq .
```

### Adım 3b — Davet kodu ile katılma

```bash
curl -s -X POST "${BASE_URL}/ayanlabs/api/property-mgmt/signup/step-3/organization/join/invite" \
  $(ayanlabs_headers_auth "${APP_ID_IPMS}") \
  -H "Content-Type: application/json" \
  -d '{"inviteCode": "INV-ABC12345"}' | jq .
```

### Adım 3b — Şirket ara ve join isteği gönder

```bash
curl -s "${BASE_URL}/ayanlabs/api/property-mgmt/signup/step-3/organization/search?name=Ayan" \
  $(ayanlabs_headers_auth "${APP_ID_IPMS}") | jq .

curl -s -X POST "${BASE_URL}/ayanlabs/api/property-mgmt/signup/step-3/organization/join/request" \
  $(ayanlabs_headers_auth "${APP_ID_IPMS}") \
  -H "Content-Type: application/json" \
  -d '{"organizationId": "<org-uuid>", "message": "I would like to join your team"}' | jq .
```

### Adım 4 — Tamamla ve dashboard'a yönlendir

```bash
curl -s -X POST "${BASE_URL}/ayanlabs/api/property-mgmt/signup/complete" \
  $(ayanlabs_headers_auth "${APP_ID_IPMS}") | jq '.data | {redirectTo, session: .session.nextStep, tokens: .tokens | {userId, orgId, profileId}}'
```

---

## Sign Up Admin — davet kodu ve join onayı

Base: `${BASE_URL}/ayanlabs/api/property-mgmt/signup/admin`  
Gerekli rol: `ORGANIZATION_ADMINISTRATOR` (JWT + org context)

### Davet kodu oluştur

```bash
curl -s -X POST "${BASE_URL}/ayanlabs/api/property-mgmt/signup/admin/invites" \
  $(ayanlabs_headers_auth "${APP_ID_IPMS}") \
  -H "Content-Type: application/json" \
  -d '{
    "invitedEmail": "new.member@example.com",
    "roleCode": "PROPERTY_MANAGER",
    "expiresInDays": 14
  }' | jq .
```

### Davet kodlarını listele

```bash
curl -s "${BASE_URL}/ayanlabs/api/property-mgmt/signup/admin/invites" \
  $(ayanlabs_headers_auth "${APP_ID_IPMS}") | jq .
```

### Bekleyen join istekleri

```bash
curl -s "${BASE_URL}/ayanlabs/api/property-mgmt/signup/admin/join-requests?status=PENDING" \
  $(ayanlabs_headers_auth "${APP_ID_IPMS}") | jq .
```

### Join isteğini onayla / reddet

```bash
JOIN_REQUEST_ID="<uuid-from-list>"

curl -s -X POST "${BASE_URL}/ayanlabs/api/property-mgmt/signup/admin/join-requests/${JOIN_REQUEST_ID}/approve" \
  $(ayanlabs_headers_auth "${APP_ID_IPMS}") \
  -H "Content-Type: application/json" \
  -d '{"decisionNote": "Welcome to the team"}' | jq .

curl -s -X POST "${BASE_URL}/ayanlabs/api/property-mgmt/signup/admin/join-requests/${JOIN_REQUEST_ID}/reject" \
  $(ayanlabs_headers_auth "${APP_ID_IPMS}") \
  -H "Content-Type: application/json" \
  -d '{"decisionNote": "No open positions"}' | jq .
```

Onay sonrası kullanıcı `POST /signup/complete` ile dashboard'a geçebilir.

---

## Generic approval review endpoints (Core)

Base: `${BASE_URL}/api/core/approvals/requests`

### Pending approvals list

```bash
curl -s "${BASE_URL}/api/core/approvals/requests?status=PENDING" \
  $(ayanlabs_headers_auth "${APP_ID_IPMS}") | jq .
```

### Approve / reject

```bash
APPROVAL_ID="<approval-request-id>"

curl -s -X POST "${BASE_URL}/api/core/approvals/requests/${APPROVAL_ID}/approve" \
  $(ayanlabs_headers_auth "${APP_ID_IPMS}") \
  -H "Content-Type: application/json" \
  -d '{"decisionNote":"All checks passed"}' | jq .

curl -s -X POST "${BASE_URL}/api/core/approvals/requests/${APPROVAL_ID}/reject" \
  $(ayanlabs_headers_auth "${APP_ID_IPMS}") \
  -H "Content-Type: application/json" \
  -d '{"decisionNote":"Document is invalid"}' | jq .
```

Not: Organization activation/deactivation approval sonuçlarına göre otomatik yönetilir.
