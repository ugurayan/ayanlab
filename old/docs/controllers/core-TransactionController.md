# TransactionController

- Module: `core`
- Source: `core/src/main/java/com/ayanlabs/core/controller/TransactionController.java`

## Endpoints

### `POST /api/core/transactions`

- Handler: `create`

```bash
curl -X POST "${BASE_URL}/api/core/transactions" \
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

### `PUT /api/core/transactions/{id}`

- Handler: `update`

```bash
curl -X PUT "${BASE_URL}/api/core/transactions/{id}" \
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

### `GET /api/core/transactions/{id}`

- Handler: `getById`

```bash
curl -X GET "${BASE_URL}/api/core/transactions/{id}" \
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

### `GET /api/core/transactions/by-idempotency-key`

- Handler: `getByIdempotencyKey`

```bash
curl -X GET "${BASE_URL}/api/core/transactions/by-idempotency-key" \
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

### `GET /api/core/transactions`

- Handler: `list`

```bash
curl -X GET "${BASE_URL}/api/core/transactions" \
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

### `DELETE /api/core/transactions/{id}`

- Handler: `deleteById`

```bash
curl -X DELETE "${BASE_URL}/api/core/transactions/{id}" \
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

