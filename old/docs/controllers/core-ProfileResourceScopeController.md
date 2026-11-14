# ProfileResourceScopeController

- Module: `core`
- Source: `core/src/main/java/com/ayanlabs/core/controller/ProfileResourceScopeController.java`

## Endpoints

### `POST /api/core/profile-resource-scopes`

- Handler: `create`

```bash
curl -X POST "${BASE_URL}/api/core/profile-resource-scopes" \
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

### `PUT /api/core/profile-resource-scopes/{id}`

- Handler: `update`

```bash
curl -X PUT "${BASE_URL}/api/core/profile-resource-scopes/{id}" \
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

### `GET /api/core/profile-resource-scopes/{id}`

- Handler: `getById`

```bash
curl -X GET "${BASE_URL}/api/core/profile-resource-scopes/{id}" \
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

### `GET /api/core/profile-resource-scopes`

- Handler: `listByProfile`

```bash
curl -X GET "${BASE_URL}/api/core/profile-resource-scopes" \
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

### `DELETE /api/core/profile-resource-scopes/{id}`

- Handler: `deleteById`

```bash
curl -X DELETE "${BASE_URL}/api/core/profile-resource-scopes/{id}" \
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

