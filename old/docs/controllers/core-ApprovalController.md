# ApprovalController

- Module: `core`
- Source: `core/src/main/java/com/ayanlabs/core/controller/ApprovalController.java`

## Endpoints

### `POST /api/core/approvals/requests`

- Handler: `create`

```bash
curl -X POST "${BASE_URL}/api/core/approvals/requests" \
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

### `GET /api/core/approvals/requests`

- Handler: `list`

```bash
curl -X GET "${BASE_URL}/api/core/approvals/requests" \
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

### `POST /api/core/approvals/requests/{id}/approve`

- Handler: `approve`

```bash
curl -X POST "${BASE_URL}/api/core/approvals/requests/{id}/approve" \
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
  "status": "APPROVED"
}
}
```

### `POST /api/core/approvals/requests/{id}/reject`

- Handler: `reject`

```bash
curl -X POST "${BASE_URL}/api/core/approvals/requests/{id}/reject" \
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
  "status": "APPROVED"
}
}
```

### `POST /api/core/approvals/requests/{id}/changes-requested`

- Handler: `changesRequested`

```bash
curl -X POST "${BASE_URL}/api/core/approvals/requests/{id}/changes-requested" \
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
  "status": "APPROVED"
}
}
```

