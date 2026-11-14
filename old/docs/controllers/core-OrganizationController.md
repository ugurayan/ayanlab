# OrganizationController

- Module: `core`
- Source: `core/src/main/java/com/ayanlabs/core/controller/OrganizationController.java`

## Endpoints

### `POST /api/core/organizations`

- Handler: `create`

```bash
curl -X POST "${BASE_URL}/api/core/organizations" \
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

### `PUT /api/core/organizations/{id}`

- Handler: `update`

```bash
curl -X PUT "${BASE_URL}/api/core/organizations/{id}" \
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

### `GET /api/core/organizations`

- Handler: `list`

```bash
curl -X GET "${BASE_URL}/api/core/organizations" \
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

### `GET /api/core/organizations/{id}`

- Handler: `getById`

```bash
curl -X GET "${BASE_URL}/api/core/organizations/{id}" \
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

### `DELETE /api/core/organizations/{id}`

- Handler: `deleteById`

```bash
curl -X DELETE "${BASE_URL}/api/core/organizations/{id}" \
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

