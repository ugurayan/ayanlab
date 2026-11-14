# Core Billing & Subscription API

Base: `${BASE_URL}/api/core`

---

## Invoices — `/invoices`

| Method | Path | Query |
|--------|------|-------|
| GET | `/invoices` | `appId`, `profileId`, `organizationId`, `status` |
| GET | `/invoices/{id}` | |
| POST | `/invoices` | |
| PUT | `/invoices/{id}` | |
| DELETE | `/invoices/{id}` | |

```bash
curl -s "${BASE_URL}/api/core/invoices?appId=${APP_ID_IPMS}&status=OPEN" \
  $(ayanlabs_headers_auth "${APP_ID_CORE}") | jq .
```

---

## Invoice line items — `/invoice-line-items`

| Method | Path | Query |
|--------|------|-------|
| GET | `/invoice-line-items` | `?invoiceId=` |
| GET | `/invoice-line-items/{id}` | |
| POST | `/invoice-line-items` | |
| PUT | `/invoice-line-items/{id}` | |
| DELETE | `/invoice-line-items/{id}` | |

---

## Payments — `/payments`

| Method | Path | Query |
|--------|------|-------|
| GET | `/payments` | `appId`, `invoiceId` |
| GET | `/payments/{id}` | |
| POST | `/payments` | |
| PUT | `/payments/{id}` | |
| DELETE | `/payments/{id}` | |

---

## Transactions — `/transactions`

| Method | Path | Query |
|--------|------|-------|
| GET | `/transactions` | `appId`, `profileId` |
| GET | `/transactions/by-idempotency-key` | `appId`, `idempotencyKey` |
| GET | `/transactions/{id}` | |
| POST | `/transactions` | |
| PUT | `/transactions/{id}` | |
| DELETE | `/transactions/{id}` | |

---

## Subscription plans — `/subscription-plans`

| Method | Path | Query |
|--------|------|-------|
| GET | `/subscription-plans` | `appId`, `activeOnly` (public token opsiyonel) |
| GET | `/subscription-plans/{id}` | |
| POST | `/subscription-plans` | |
| PUT | `/subscription-plans/{id}` | |
| DELETE | `/subscription-plans/{id}` | |

```bash
# Public catalog
curl -s "${BASE_URL}/api/core/subscription-plans?appId=${APP_ID_IPMS}&activeOnly=true" \
  $(ayanlabs_headers_public "${APP_ID_IPMS}") | jq .
```

---

## Plan features — `/subscription-plan-features`

| Method | Path | Query |
|--------|------|-------|
| GET | `/subscription-plan-features` | `?planId=` |
| GET/POST/PUT/DELETE | `/{id}` | |

---

## Plan prices — `/subscription-plan-prices`

| Method | Path | Query |
|--------|------|-------|
| GET | `/subscription-plan-prices` | `?planId=` |
| GET/POST/PUT/DELETE | `/{id}` | |

---

## Profile subscriptions — `/profile-subscriptions`

| Method | Path | Not |
|--------|------|-----|
| GET | `/profile-subscriptions/catalog` | Public, `?appId=` |
| GET | `/profile-subscriptions` | `?profileId=` |
| GET | `/profile-subscriptions/{id}` | |
| POST | `/profile-subscriptions/start` | Abonelik başlat |
| DELETE | `/profile-subscriptions/{id}` | |

```bash
curl -s "${BASE_URL}/api/core/profile-subscriptions/catalog?appId=${APP_ID_VAULT}" \
  $(ayanlabs_headers_public "${APP_ID_VAULT}") | jq .
```
