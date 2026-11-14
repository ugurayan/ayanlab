# PolicyGroupController

- Module: `core`
- Source: `core/src/main/java/com/ayanlabs/core/controller/PolicyGroupController.java`

## Endpoints

### `POST /api/core/policy-groups`

- Handler: `create`

```bash
curl -X POST "${BASE_URL}/api/core/policy-groups" \
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

### `PUT /api/core/policy-groups/{id}`

- Handler: `update`

```bash
curl -X PUT "${BASE_URL}/api/core/policy-groups/{id}" \
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

### `GET /api/core/policy-groups/{id}`

- Handler: `getById`

```bash
curl -X GET "${BASE_URL}/api/core/policy-groups/{id}" \
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

### `GET /api/core/policy-groups`

- Handler: `listByApp`

```bash
curl -X GET "${BASE_URL}/api/core/policy-groups" \
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

### `DELETE /api/core/policy-groups/{id}`

- Handler: `deleteById`

```bash
curl -X DELETE "${BASE_URL}/api/core/policy-groups/{id}" \
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

