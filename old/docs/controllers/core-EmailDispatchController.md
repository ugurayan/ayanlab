# EmailDispatchController

- Module: `core`
- Source: `core/src/main/java/com/ayanlabs/core/controller/EmailDispatchController.java`

## Endpoints

### `POST /api/core/emails/send-template`

- Handler: `sendTemplate`

```bash
curl -X POST "${BASE_URL}/api/core/emails/send-template" \
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

### `POST /api/core/emails/preview-template`

- Handler: `previewTemplate`

```bash
curl -X POST "${BASE_URL}/api/core/emails/preview-template" \
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

