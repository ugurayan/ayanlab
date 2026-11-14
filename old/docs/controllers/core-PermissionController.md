# PermissionController

- Module: `core`
- Source: `core/src/main/java/com/ayanlabs/core/controller/PermissionController.java`

## Endpoints

### `POST /api/core/permissions`

- Handler: `create`

```bash
curl -X POST "${BASE_URL}/api/core/permissions" \
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

### `PUT /api/core/permissions/{id}`

- Handler: `update`

```bash
curl -X PUT "${BASE_URL}/api/core/permissions/{id}" \
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

### `GET /api/core/permissions`

- Handler: `list`

```bash
curl -X GET "${BASE_URL}/api/core/permissions" \
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

### `GET /api/core/permissions/{id}`

- Handler: `getById`

```bash
curl -X GET "${BASE_URL}/api/core/permissions/{id}" \
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

### `DELETE /api/core/permissions/{id}`

- Handler: `deleteById`

```bash
curl -X DELETE "${BASE_URL}/api/core/permissions/{id}" \
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

