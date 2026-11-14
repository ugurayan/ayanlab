# StaffIQ (Rideshare) — curl örnekleri

Base: `${BASE_URL}/ayanlabs/api/rideshare`  
App ID: `APP_ID_STAFFIQ` = `f01ae844-85fd-48a0-90ed-8bdd7e0e48a6`

Önce: `source docs/curl/env.example.sh` ve token ayarlayın.

---

## Public — health

```bash
curl -s "${BASE_URL}/ayanlabs/api/rideshare/health" \
  $(ayanlabs_headers_public "${APP_ID_STAFFIQ}") | jq .
```

---

## Public — subscription planları

```bash
curl -s "${BASE_URL}/ayanlabs/api/rideshare/subscriptions/plans" \
  $(ayanlabs_headers_public "${APP_ID_STAFFIQ}") | jq .
```

---

## Auth — authorization context

```bash
curl -s "${BASE_URL}/ayanlabs/api/rideshare/authorization/context" \
  $(ayanlabs_headers_auth "${APP_ID_STAFFIQ}") | jq .
```

---

## Auth — ride request (POST örneği)

```bash
curl -s -X POST "${BASE_URL}/ayanlabs/api/rideshare/rides/request" \
  $(ayanlabs_headers_auth "${APP_ID_STAFFIQ}") \
  -H "Content-Type: application/json" \
  -d '{
    "pickupAddress": "Downtown Station",
    "dropoffAddress": "Airport Terminal 1"
  }' | jq .
```
