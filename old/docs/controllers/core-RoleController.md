# RoleController

- Module: `core`
- Source: `core/src/main/java/com/ayanlabs/core/controller/RoleController.java`

## Endpoints

### `POST /api/core/roles`

- Handler: `create`

```bash
curl -X POST "${BASE_URL}/api/core/roles" \
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

### `PUT /api/core/roles/{id}`

- Handler: `update`

```bash
curl -X PUT "${BASE_URL}/api/core/roles/{id}" \
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

### `GET /api/core/roles`

- Handler: `list`

```bash
curl -X GET "${BASE_URL}/api/core/roles" \
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

### `GET /api/core/roles/{id}`

- Handler: `getById`

```bash
curl -X GET "${BASE_URL}/api/core/roles/{id}" \
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

### `DELETE /api/core/roles/{id}`

- Handler: `deleteById`

```bash
curl -X DELETE "${BASE_URL}/api/core/roles/{id}" \
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

