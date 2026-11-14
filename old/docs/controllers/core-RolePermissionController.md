# RolePermissionController

- Module: `core`
- Source: `core/src/main/java/com/ayanlabs/core/controller/RolePermissionController.java`

## Endpoints

### `POST /api/core/role-permissions`

- Handler: `create`

```bash
curl -X POST "${BASE_URL}/api/core/role-permissions" \
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

### `PUT /api/core/role-permissions/{id}`

- Handler: `update`

```bash
curl -X PUT "${BASE_URL}/api/core/role-permissions/{id}" \
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

### `GET /api/core/role-permissions`

- Handler: `listByRole`

```bash
curl -X GET "${BASE_URL}/api/core/role-permissions" \
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

### `GET /api/core/role-permissions/{id}`

- Handler: `getById`

```bash
curl -X GET "${BASE_URL}/api/core/role-permissions/{id}" \
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

### `DELETE /api/core/role-permissions/{id}`

- Handler: `deleteById`

```bash
curl -X DELETE "${BASE_URL}/api/core/role-permissions/{id}" \
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

