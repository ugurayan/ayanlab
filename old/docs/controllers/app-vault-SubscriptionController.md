# SubscriptionController

- Module: `app-vault`
- Source: `app-vault/src/main/java/com/ayanlabs/vault/controller/SubscriptionController.java`

## Endpoints

### `GET /ayanlabs/api/vault/subscriptions/plans`

- Handler: `plans`

```bash
curl -X GET "${BASE_URL}/ayanlabs/api/vault/subscriptions/plans" \
  $(ayanlabs_headers_auth "${APP_ID}")
```

#### Example Response

```json
{
  "success": true,
  "message": "OK",
  "data": [
  {
    "id": "uuid",
    "isActive": true
  }
]
}
```

### `GET /ayanlabs/api/vault/subscriptions/users/me`

- Handler: `mySubscriptions`

```bash
curl -X GET "${BASE_URL}/ayanlabs/api/vault/subscriptions/users/me" \
  $(ayanlabs_headers_auth "${APP_ID}")
```

#### Example Response

```json
{
  "success": true,
  "message": "OK",
  "data": [
  {
    "id": "uuid",
    "isActive": true
  }
]
}
```

### `POST /ayanlabs/api/vault/subscriptions/users/me`

- Handler: `startMySubscription`

```bash
curl -X POST "${BASE_URL}/ayanlabs/api/vault/subscriptions/users/me" \
  $(ayanlabs_headers_auth "${APP_ID}") \
  -H "Content-Type: application/json" \
  -d '{}'
```

#### Example Response

```json
{
  "success": true,
  "message": "OK",
  "data": {
  "id": "uuid",
  "isActive": true
}
}
```

### `GET /ayanlabs/api/vault/subscriptions/organizations/{organizationId}`

- Handler: `organizationSubscriptions`

```bash
curl -X GET "${BASE_URL}/ayanlabs/api/vault/subscriptions/organizations/{organizationId}" \
  $(ayanlabs_headers_auth "${APP_ID}")
```

#### Example Response

```json
{
  "success": true,
  "message": "OK",
  "data": [
  {
    "id": "uuid",
    "isActive": true
  }
]
}
```

### `POST /ayanlabs/api/vault/subscriptions/organizations/{organizationId}`

- Handler: `startOrganizationSubscription`

```bash
curl -X POST "${BASE_URL}/ayanlabs/api/vault/subscriptions/organizations/{organizationId}" \
  $(ayanlabs_headers_auth "${APP_ID}") \
  -H "Content-Type: application/json" \
  -d '{}'
```

#### Example Response

```json
{
  "success": true,
  "message": "OK",
  "data": {
  "id": "uuid",
  "isActive": true
}
}
```

