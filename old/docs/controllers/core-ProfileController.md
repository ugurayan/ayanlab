# ProfileController

- Module: `core`
- Source: `core/src/main/java/com/ayanlabs/core/controller/ProfileController.java`

## Endpoints

### `POST /api/core/profiles`

- Handler: `create`

```bash
curl -X POST "${BASE_URL}/api/core/profiles" \
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

### `PUT /api/core/profiles/{id}`

- Handler: `update`

```bash
curl -X PUT "${BASE_URL}/api/core/profiles/{id}" \
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

### `GET /api/core/profiles`

- Handler: `list`

```bash
curl -X GET "${BASE_URL}/api/core/profiles" \
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

### `GET /api/core/profiles/{id}`

- Handler: `getById`

```bash
curl -X GET "${BASE_URL}/api/core/profiles/{id}" \
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

### `DELETE /api/core/profiles/{id}`

- Handler: `deleteById`

```bash
curl -X DELETE "${BASE_URL}/api/core/profiles/{id}" \
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

