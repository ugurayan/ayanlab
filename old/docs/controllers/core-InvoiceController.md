# InvoiceController

- Module: `core`
- Source: `core/src/main/java/com/ayanlabs/core/controller/InvoiceController.java`

## Endpoints

### `POST /api/core/invoices`

- Handler: `create`

```bash
curl -X POST "${BASE_URL}/api/core/invoices" \
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

### `PUT /api/core/invoices/{id}`

- Handler: `update`

```bash
curl -X PUT "${BASE_URL}/api/core/invoices/{id}" \
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

### `GET /api/core/invoices/{id}`

- Handler: `getById`

```bash
curl -X GET "${BASE_URL}/api/core/invoices/{id}" \
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

### `GET /api/core/invoices`

- Handler: `list`

```bash
curl -X GET "${BASE_URL}/api/core/invoices" \
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

### `DELETE /api/core/invoices/{id}`

- Handler: `deleteById`

```bash
curl -X DELETE "${BASE_URL}/api/core/invoices/{id}" \
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

