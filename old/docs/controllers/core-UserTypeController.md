# UserTypeController

- Module: `core`
- Source: `core/src/main/java/com/ayanlabs/core/controller/UserTypeController.java`

## Endpoints

### `POST /api/core/user-types`

- Handler: `create`

```bash
curl -X POST "${BASE_URL}/api/core/user-types" \
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

### `PUT /api/core/user-types/{id}`

- Handler: `update`

```bash
curl -X PUT "${BASE_URL}/api/core/user-types/{id}" \
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

### `GET /api/core/user-types`

- Handler: `list`

```bash
curl -X GET "${BASE_URL}/api/core/user-types" \
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

### `GET /api/core/user-types/{id}`

- Handler: `getById`

```bash
curl -X GET "${BASE_URL}/api/core/user-types/{id}" \
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

### `DELETE /api/core/user-types/{id}`

- Handler: `deleteById`

```bash
curl -X DELETE "${BASE_URL}/api/core/user-types/{id}" \
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

