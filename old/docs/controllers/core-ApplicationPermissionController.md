# ApplicationPermissionController

- Module: `core`
- Source: `core/src/main/java/com/ayanlabs/core/controller/ApplicationPermissionController.java`

## Endpoints

### `POST /api/core/application-permissions`

- Handler: `create`

```bash
curl -X POST "${BASE_URL}/api/core/application-permissions" \
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

### `PUT /api/core/application-permissions/{id}`

- Handler: `update`

```bash
curl -X PUT "${BASE_URL}/api/core/application-permissions/{id}" \
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

### `GET /api/core/application-permissions/{id}`

- Handler: `getById`

```bash
curl -X GET "${BASE_URL}/api/core/application-permissions/{id}" \
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

### `GET /api/core/application-permissions`

- Handler: `listByApp`

```bash
curl -X GET "${BASE_URL}/api/core/application-permissions" \
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

### `DELETE /api/core/application-permissions/{id}`

- Handler: `deleteById`

```bash
curl -X DELETE "${BASE_URL}/api/core/application-permissions/{id}" \
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

