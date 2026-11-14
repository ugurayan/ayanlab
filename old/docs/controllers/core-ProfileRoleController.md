# ProfileRoleController

- Module: `core`
- Source: `core/src/main/java/com/ayanlabs/core/controller/ProfileRoleController.java`

## Endpoints

### `POST /api/core/profile-roles`

- Handler: `create`

```bash
curl -X POST "${BASE_URL}/api/core/profile-roles" \
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

### `PUT /api/core/profile-roles/{id}`

- Handler: `update`

```bash
curl -X PUT "${BASE_URL}/api/core/profile-roles/{id}" \
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

### `GET /api/core/profile-roles/{id}`

- Handler: `getById`

```bash
curl -X GET "${BASE_URL}/api/core/profile-roles/{id}" \
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

### `GET /api/core/profile-roles`

- Handler: `listByProfile`

```bash
curl -X GET "${BASE_URL}/api/core/profile-roles" \
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

### `DELETE /api/core/profile-roles/{id}`

- Handler: `deleteById`

```bash
curl -X DELETE "${BASE_URL}/api/core/profile-roles/{id}" \
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

