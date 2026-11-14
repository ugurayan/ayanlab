# UserController

- Module: `core`
- Source: `core/src/main/java/com/ayanlabs/core/controller/UserController.java`

## Endpoints

### `POST /api/core/users`

- Handler: `create`

```bash
curl -X POST "${BASE_URL}/api/core/users" \
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

### `PUT /api/core/users/{id}`

- Handler: `update`

```bash
curl -X PUT "${BASE_URL}/api/core/users/{id}" \
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

### `GET /api/core/users`

- Handler: `list`

```bash
curl -X GET "${BASE_URL}/api/core/users" \
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

### `GET /api/core/users/{id}`

- Handler: `getById`

```bash
curl -X GET "${BASE_URL}/api/core/users/{id}" \
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

### `DELETE /api/core/users/{id}`

- Handler: `deleteById`

```bash
curl -X DELETE "${BASE_URL}/api/core/users/{id}" \
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

