# ProfileSubscriptionController

- Module: `core`
- Source: `core/src/main/java/com/ayanlabs/core/controller/ProfileSubscriptionController.java`

## Endpoints

### `GET /api/core/profile-subscriptions/catalog`

- Handler: `catalog`

```bash
curl -X GET "${BASE_URL}/api/core/profile-subscriptions/catalog" \
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

### `POST /api/core/profile-subscriptions/start`

- Handler: `start`

```bash
curl -X POST "${BASE_URL}/api/core/profile-subscriptions/start" \
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

### `GET /api/core/profile-subscriptions`

- Handler: `listByProfile`

```bash
curl -X GET "${BASE_URL}/api/core/profile-subscriptions" \
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

### `GET /api/core/profile-subscriptions/{id}`

- Handler: `getById`

```bash
curl -X GET "${BASE_URL}/api/core/profile-subscriptions/{id}" \
  $(ayanlabs_headers_auth "${APP_ID}")
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

### `DELETE /api/core/profile-subscriptions/{id}`

- Handler: `deleteById`

```bash
curl -X DELETE "${BASE_URL}/api/core/profile-subscriptions/{id}" \
  $(ayanlabs_headers_auth "${APP_ID}")
```

#### Example Response

```json
{
  "success": true,
  "message": "OK",
  "data": null
}
```

