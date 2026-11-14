# ModuleController

- Module: `core`
- Source: `core/src/main/java/com/ayanlabs/core/controller/ModuleController.java`

## Endpoints

### `POST /api/core/modules`

- Handler: `create`

```bash
curl -X POST "${BASE_URL}/api/core/modules" \
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

### `PUT /api/core/modules/{id}`

- Handler: `update`

```bash
curl -X PUT "${BASE_URL}/api/core/modules/{id}" \
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

### `GET /api/core/modules`

- Handler: `list`

```bash
curl -X GET "${BASE_URL}/api/core/modules" \
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

### `GET /api/core/modules/{id}`

- Handler: `getById`

```bash
curl -X GET "${BASE_URL}/api/core/modules/{id}" \
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

### `DELETE /api/core/modules/{id}`

- Handler: `deleteById`

```bash
curl -X DELETE "${BASE_URL}/api/core/modules/{id}" \
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

