# PaymentController

- Module: `core`
- Source: `core/src/main/java/com/ayanlabs/core/controller/PaymentController.java`

## Endpoints

### `POST /api/core/payments`

- Handler: `create`

```bash
curl -X POST "${BASE_URL}/api/core/payments" \
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

### `PUT /api/core/payments/{id}`

- Handler: `update`

```bash
curl -X PUT "${BASE_URL}/api/core/payments/{id}" \
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

### `GET /api/core/payments/{id}`

- Handler: `getById`

```bash
curl -X GET "${BASE_URL}/api/core/payments/{id}" \
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

### `GET /api/core/payments`

- Handler: `list`

```bash
curl -X GET "${BASE_URL}/api/core/payments" \
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

### `DELETE /api/core/payments/{id}`

- Handler: `deleteById`

```bash
curl -X DELETE "${BASE_URL}/api/core/payments/{id}" \
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

