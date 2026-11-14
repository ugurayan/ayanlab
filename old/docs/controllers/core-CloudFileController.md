# CloudFileController

- Module: `core`
- Source: `core/src/main/java/com/ayanlabs/core/controller/CloudFileController.java`

## Endpoints

### `POST /api/core/files/upload`

- Handler: `upload`

```bash
curl -X POST "${BASE_URL}/api/core/files/upload" \\
  $(ayanlabs_headers_auth "${APP_ID}") \\
  -F "file=@/tmp/sample.pdf" \\
  -F "folder=common"
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

### `GET /api/core/files`

- Handler: `list`

```bash
curl -X GET "${BASE_URL}/api/core/files" \
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

### `GET /api/core/files/{id}/signed-url/read`

- Handler: `signedReadUrl`

```bash
curl -X GET "${BASE_URL}/api/core/files/{id}/signed-url/read" \
  $(ayanlabs_headers_auth "${APP_ID}")
```

#### Example Response

```json
{
  "success": true,
  "message": "OK",
  "data": {
  "url": "https://storage.googleapis.com/..."
}
}
```

### `GET /api/core/files/{id}/signed-url/write`

- Handler: `signedWriteUrl`

```bash
curl -X GET "${BASE_URL}/api/core/files/{id}/signed-url/write" \
  $(ayanlabs_headers_auth "${APP_ID}")
```

#### Example Response

```json
{
  "success": true,
  "message": "OK",
  "data": {
  "url": "https://storage.googleapis.com/..."
}
}
```

### `POST /api/core/files/{id}/approve`

- Handler: `approve`

```bash
curl -X POST "${BASE_URL}/api/core/files/{id}/approve" \
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

